package com.fusiotec.servicecenterapi.servicecenter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fusiotec.servicecenterapi.servicecenter.R;
import com.fusiotec.servicecenterapi.servicecenter.manager.ImageManager;
import com.fusiotec.servicecenterapi.servicecenter.manager.LocalStorage;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.Accounts;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.Stations;
import com.fusiotec.servicecenterapi.servicecenter.network.RetrofitRequestManager;
import com.fusiotec.servicecenterapi.servicecenter.utilities.Constants;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import io.realm.Realm;

import static com.fusiotec.servicecenterapi.servicecenter.network.RetrofitRequestManager.REQUEST_SUCCESS;

public class Dashboard extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public final static int GET_ORIGINAL_DATE = 1;

    DrawerLayout drawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        handlerExit = new Handler();
        requestManager = new RetrofitRequestManager(this,callBackListener);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(accounts.getAccount_type_id() == 1){
            navigationView.getMenu().findItem(R.id.received_job_orders).setVisible(false);
            navigationView.getMenu().findItem(R.id.new_job_orders).setVisible(true);
            navigationView.getMenu().findItem(R.id.receive_for_return).setVisible(true);
        }else if(accounts.getAccount_type_id() == 2){
            navigationView.getMenu().findItem(R.id.received_job_orders).setVisible(true);
            navigationView.getMenu().findItem(R.id.new_job_orders).setVisible(false);
            navigationView.getMenu().findItem(R.id.receive_for_return).setVisible(false);
        }

        View header = navigationView.getHeaderView(0);
        ImageView iv_profile = (ImageView) header.findViewById(R.id.iv_profile);
        TextView tv_name = (TextView) header.findViewById(R.id.tv_name);
        TextView tv_email = (TextView) header.findViewById(R.id.tv_email);
        tv_name.setText(accounts.getLast_name()+", "+accounts.getFirst_name());
        tv_email.setText(accounts.getEmail());
        ImageManager.PicassoLoadThumbnail(this, Constants.webservice_address,accounts.getImage(),iv_profile,R.drawable.profile_unknown);

        getOriginalDate();
    }

    RetrofitRequestManager requestManager;
    public void getOriginalDate(){
        requestManager.setRequestAsync(requestManager.getApiService().kroid_get_original_date(),GET_ORIGINAL_DATE);
    }

    RetrofitRequestManager.callBackListener callBackListener = new RetrofitRequestManager.callBackListener(){
        @Override
        public void requestReceiver(String response, int process, int status,int response_code,String message){
            if(response_code == REQUEST_SUCCESS){
                setReceiver(response,process,status);
            }
        }
    };
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
    public void onBackPressed() {
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
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item){
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id){
            case R.id.new_job_orders:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        newJobOrders();
                    }
                },300);
                break;
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
            case R.id.job_orders:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showJobOrders(JobOrderListActivity.SHOW_OPEN_JOB_ORDERS);
                    }
                },300);
                break;
            case R.id.history:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showJobOrders(JobOrderListActivity.SHOW_HISTORY);
                    }
                },300);
                break;
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
            case R.id.profile:
                break;
            case R.id.settings:
                break;
            case R.id.log_out:
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.delete(Accounts.class);
//                        realm.delete(Customers.class);
//                        realm.delete(JobOrderDiagnosis.class);
//                        realm.delete(JobOrderImages.class);
//                        realm.delete(JobOrders.class);
//                        realm.delete(JobOrderShipping.class);
                        realm.delete(Stations.class);
                    }
                });
                Intent in = new Intent(Dashboard.this,LoginActivity.class);
                startActivity(in);
                finish();
                overridePendingTransition(R.anim.top_in, R.anim.freeze);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
