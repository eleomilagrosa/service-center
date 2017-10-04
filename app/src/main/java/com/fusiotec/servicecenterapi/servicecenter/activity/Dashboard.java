package com.fusiotec.servicecenterapi.servicecenter.activity;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fusiotec.servicecenterapi.servicecenter.R;
import com.fusiotec.servicecenterapi.servicecenter.manager.ImageManager;
import com.fusiotec.servicecenterapi.servicecenter.manager.LocalStorage;
import com.fusiotec.servicecenterapi.servicecenter.manager.PhotoUploader;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.Accounts;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.Customers;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.JobOrderDiagnosis;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.JobOrderForReturn;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.JobOrderImages;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.JobOrderRepairStatus;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.JobOrderShipping;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.JobOrders;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.Stations;
import com.fusiotec.servicecenterapi.servicecenter.network.RetrofitRequestManager;
import com.fusiotec.servicecenterapi.servicecenter.utilities.Constants;
import com.fusiotec.servicecenterapi.servicecenter.utilities.RealmModelGenericHelper;
import com.fusiotec.servicecenterapi.servicecenter.utilities.Utils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.fusiotec.servicecenterapi.servicecenter.network.RetrofitRequestManager.REQUEST_SUCCESS;

public class Dashboard extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener {

    public final static int GET_ORIGINAL_DATE = 1;

    DrawerLayout drawer;
    TextView tv_upload_images;
    RelativeLayout rl_receive_at_main,rl_new_job_order,rl_job_order,rl_receive_at_sc,rl_customers,rl_history,rl_upload;

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        ls.saveIntegerOnLocalStorage(LocalStorage.SCREEN_WIDTH,width);

        handlerExit = new Handler();

        initUI();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(accounts.getAccount_type_id() == Accounts.SERVICE_CENTER){
            navigationView.getMenu().findItem(R.id.received_job_orders).setVisible(false);
            navigationView.getMenu().findItem(R.id.new_job_orders).setVisible(true);
            navigationView.getMenu().findItem(R.id.receive_for_return).setVisible(true);
            navigationView.getMenu().findItem(R.id.branches).setVisible(false);
        }else if(accounts.getAccount_type_id() == Accounts.MAIN_BRANCH){
            navigationView.getMenu().findItem(R.id.received_job_orders).setVisible(true);
            navigationView.getMenu().findItem(R.id.new_job_orders).setVisible(false);
            navigationView.getMenu().findItem(R.id.receive_for_return).setVisible(false);
            navigationView.getMenu().findItem(R.id.branches).setVisible(true);
        }
        if(accounts.getIs_main_branch() > 0){
                navigationView.getMenu().findItem(R.id.accounts).setVisible(true);
                navigationView.getMenu().findItem(R.id.approved_accounts).setVisible(true);
            if(accounts.getIs_main_branch() == 1){
                navigationView.getMenu().findItem(R.id.admins).setVisible(false);
            }else{
                navigationView.getMenu().findItem(R.id.admins).setVisible(true);
            }
        }else{
            navigationView.getMenu().findItem(R.id.accounts).setVisible(false);
            navigationView.getMenu().findItem(R.id.approved_accounts).setVisible(false);
            navigationView.getMenu().findItem(R.id.admins).setVisible(false);
        }

        View header = navigationView.getHeaderView(0);
        ImageView iv_profile = header.findViewById(R.id.iv_profile);
        TextView tv_name = header.findViewById(R.id.tv_name);
        TextView tv_email = header.findViewById(R.id.tv_email);
        tv_name.setText(accounts.getLast_name()+", "+accounts.getFirst_name());
        tv_email.setText(accounts.getEmail());
        ImageManager.PicassoLoadThumbnail(this, Constants.webservice_address,accounts.getImage(),iv_profile,R.drawable.profile_unknown);

        getOriginalDate();
    }
    public void initUI(){
        rl_receive_at_main = (RelativeLayout) findViewById(R.id.rl_receive_at_main);
        rl_new_job_order = (RelativeLayout) findViewById(R.id.rl_new_job_order);
        rl_job_order = (RelativeLayout) findViewById(R.id.rl_job_order);
        rl_receive_at_sc = (RelativeLayout) findViewById(R.id.rl_receive_at_sc);
        rl_customers = (RelativeLayout) findViewById(R.id.rl_customers);
        rl_history = (RelativeLayout) findViewById(R.id.rl_history);
        rl_upload = (RelativeLayout) findViewById(R.id.rl_upload);
        tv_upload_images = (TextView) findViewById(R.id.tv_upload_images);

        rl_receive_at_main.setOnClickListener(this);
        rl_new_job_order.setOnClickListener(this);
        rl_job_order.setOnClickListener(this);
        rl_receive_at_sc.setOnClickListener(this);
        rl_customers.setOnClickListener(this);
        rl_history.setOnClickListener(this);
        rl_upload.setOnClickListener(this);

        if(accounts.getAccount_type_id() == Accounts.SERVICE_CENTER){
            rl_receive_at_main.setVisibility(View.GONE);
            rl_new_job_order.setVisibility(View.VISIBLE);
            rl_receive_at_sc.setVisibility(View.VISIBLE);
        }else if(accounts.getAccount_type_id() == Accounts.MAIN_BRANCH){
            rl_receive_at_main.setVisibility(View.VISIBLE);
            rl_new_job_order.setVisibility(View.GONE);
            rl_receive_at_sc.setVisibility(View.GONE);
        }

        initUploadListener();
    }
    RealmResults<JobOrderImages> upload_images;
    public void initUploadListener(){
        handler = new Handler();
        upload_images = realm.where(JobOrderImages.class).lessThan("id",0).findAll();
        reloadUploadButton();
        new RealmModelGenericHelper<JobOrderImages>().initClassForChanges(JobOrderImages.class,upload_images , listener);
    }
    RealmModelGenericHelper.RealmModelChangeListener listener = new RealmModelGenericHelper.RealmModelChangeListener() {
        @Override
        public void showUnsyncRows(int size){
            handler.removeCallbacks(showRows);
            handler.postDelayed(showRows,1000);
        }
    };
    Runnable showRows = new Runnable(){
        @Override
        public void run(){
            reloadUploadButton();
        }
    };
    public void reloadUploadButton(){
        tv_upload_images.setText("Upload "+upload_images.size()+" Unsaved Images");
    }
    public void getOriginalDate(){
        requestManager.setRequestAsync(requestManager.getApiService().get_original_date(),GET_ORIGINAL_DATE);
    }

    public void setReceiver(String response,int process,int status){
        Log.e(process + "response" + status, response);
        switch(process){
            case GET_ORIGINAL_DATE:
                setTimeDifference(response);
                break;
        }
    }
    public void setTimeDifference(String response){
        LocalStorage ls = new LocalStorage(this);
        JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
        String date = jsonObject.get("original_date").getAsString();
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        DateTime server_datetime = fmt.parseDateTime(date);
        DateTime device_dateTime = new DateTime();
        Seconds sec = Seconds.secondsBetween(device_dateTime,server_datetime);
        ls.saveIntegerOnLocalStorage(LocalStorage.TIME_DIFFERENCE_IN_SECONDS,sec.getSeconds());
    }


    boolean is_exiting = false;
    @Override
    public void onBackPressed(){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(is_exiting){
                super.onBackPressed();
            }else{
                Toast.makeText(this, "Press Back Again to Exit", Toast.LENGTH_SHORT).show();
                is_exiting = true;
                doubleTap();
            }
        }
    }
    Handler handlerExit;
    public void doubleTap(){
        handlerExit.postDelayed(new Runnable() {
            @Override
            public void run() {
                is_exiting = false;
            }
        }, 2500);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item){
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        clickMenu(id);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onClick(View view){
        int id = view.getId();
        clickMenu(id);
    }
    public void clickMenu(int id){
        switch (id){
            case R.id.reports:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent in = new Intent(Dashboard.this, ReportActivity.class);
                        startActivity(in);
                        overridePendingTransition(R.anim.left_in, R.anim.right_out);
                    }
                },300);
                break;
            case R.id.rl_upload:
                Toast.makeText(this, "Uploading "+upload_images.size()+" images" , Toast.LENGTH_SHORT).show();
                Utils.syncImages(this);
                break;
            case R.id.rl_new_job_order:
            case R.id.new_job_orders:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        newJobOrders();
                    }
                },300);
                break;
            case R.id.rl_receive_at_main:
            case R.id.received_job_orders:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent in = new Intent(Dashboard.this, ViewJobOrderActivity.class);
                        in.putExtra(ViewJobOrderActivity.JOB_ORDER_STATUS,ViewJobOrderActivity.RECEIVED_IN_MAIN);
                        startActivity(in);
                        overridePendingTransition(R.anim.left_in, R.anim.right_out);
                    }
                },300);
                break;
            case R.id.rl_receive_at_sc:
            case R.id.receive_for_return:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent in = new Intent(Dashboard.this, ViewJobOrderActivity.class);
                        in.putExtra(ViewJobOrderActivity.JOB_ORDER_STATUS,ViewJobOrderActivity.RECEIVED_IN_MAIN);
                        startActivity(in);
                        overridePendingTransition(R.anim.left_in, R.anim.right_out);
                    }
                },300);
                break;
            case R.id.rl_job_order:
            case R.id.job_orders:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showJobOrders(JobOrderListActivity.SHOW_OPEN_JOB_ORDERS);
                    }
                },300);
                break;
            case R.id.rl_history:
            case R.id.history:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showJobOrders(JobOrderListActivity.SHOW_HISTORY);
                    }
                },300);
                break;
            case R.id.rl_customers:
            case R.id.customers:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent in = new Intent(Dashboard.this, CustomerListActivity.class);
                        startActivity(in);
                        overridePendingTransition(R.anim.left_in, R.anim.right_out);
                    }
                },300);

                break;
            case R.id.accounts:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent accountActivity = new Intent(Dashboard.this,AccountListActivity.class);
                        accountActivity.putExtra(AccountListActivity.APPROVED,true);
                        startActivity(accountActivity);
                        overridePendingTransition(R.anim.top_in, R.anim.freeze);
                    }
                },300);
                break;
            case R.id.approved_accounts:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent accountActivity2 = new Intent(Dashboard.this,AccountListActivity.class);
                        accountActivity2.putExtra(AccountListActivity.APPROVED,false);
                        startActivity(accountActivity2);
                        overridePendingTransition(R.anim.top_in, R.anim.freeze);
                    }
                },300);
                break;
            case R.id.branches:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent in = new Intent(Dashboard.this,BranchListActivity.class);
                        startActivity(in);
                        overridePendingTransition(R.anim.top_in, R.anim.freeze);
                    }
                },300);
                break;
            case R.id.admins:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent in = new Intent(Dashboard.this,AdminListActivity.class);
                        startActivity(in);
                        overridePendingTransition(R.anim.top_in, R.anim.freeze);
                    }
                },300);
                break;
            case R.id.profile:
                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run(){
                        Intent in = new Intent(Dashboard.this,RegistrationActivity.class);
                        in.putExtra(RegistrationActivity.ACCOUNT_ID,accounts.getId());
                        in.putExtra(RegistrationActivity.IS_PROFILE,true);
                        startActivity(in);
                        overridePendingTransition(R.anim.top_in, R.anim.freeze);
                    }
                },300);
                break;
            case R.id.settings:
                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run(){
                        Intent in = new Intent(Dashboard.this,ChangePasswordActivity.class);
                        startActivity(in);
                        overridePendingTransition(R.anim.top_in, R.anim.freeze);
                    }
                },300);
                break;
            case R.id.printer:
                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run(){
                        Intent in = new Intent(Dashboard.this,PrinterSettingsActivity.class);
                        startActivity(in);
                        overridePendingTransition(R.anim.top_in, R.anim.freeze);
                    }
                },300);
                break;
            case R.id.log_out:
                realm.executeTransaction(new Realm.Transaction(){
                    @Override
                    public void execute(Realm realm) {
                        realm.delete(Accounts.class);
                        realm.delete(Customers.class);
                        realm.delete(JobOrderDiagnosis.class);
                        realm.delete(JobOrderForReturn.class);
//                        realm.delete(JobOrderImages.class);
                        realm.delete(JobOrderRepairStatus.class);
                        realm.delete(JobOrders.class);
                        realm.delete(JobOrderShipping.class);
//                        realm.delete(Stations.class);
                    }
                });
                ls.saveIntegerOnLocalStorage(LocalStorage.ACCOUNT_ID,0);
                Intent in = new Intent(Dashboard.this,LoginActivity.class);
                startActivity(in);
                finish();
                overridePendingTransition(R.anim.top_in, R.anim.freeze);

                break;
        }
    }

    public void newJobOrders(){
        Intent in = new Intent(this,NewJobOrderActivity.class);
        startActivity(in);
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }
    public void showJobOrders(int show_type){
        Intent in = new Intent(this,JobOrderListActivity.class);
        in.putExtra(JobOrderListActivity.SHOW,show_type);
        startActivity(in);
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }
}
