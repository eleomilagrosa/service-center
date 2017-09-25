package com.fusiotec.servicecenterapi.servicecenter.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.claudiodegio.msv.OnSearchViewListener;
import com.fusiotec.servicecenterapi.servicecenter.R;
import com.fusiotec.servicecenterapi.servicecenter.customviews.CustomSuggestionSearch;
import com.fusiotec.servicecenterapi.servicecenter.fragments.ImageViewerFragment;
import com.fusiotec.servicecenterapi.servicecenter.fragments.JobOrderSummaryFragment;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.JobOrderImages;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.JobOrders;
import com.fusiotec.servicecenterapi.servicecenter.models.serialize_object.JobOrderSerialize;
import com.fusiotec.servicecenterapi.servicecenter.network.RetrofitRequestManager;
import com.fusiotec.servicecenterapi.servicecenter.utilities.Utils;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Owner on 8/13/2017.
 */

public class ViewJobOrderActivity extends BaseActivity implements
        JobOrderSummaryFragment.JobOrderSummaryFragmentListener,
        ImageViewerFragment.ImageViewerActivityListener{

    public final static int FRAGMENT_JOB_ORDER_SUMMARY = 1;
    public final static int CLOSE_JOB_ORDER = 2;
    public final static int FRAGMENT_JOB_ORDER_VIEW_IMGAGES = 3;
    public final static int RESTART_ACTIVITY = 4;

    int current_fragment = FRAGMENT_JOB_ORDER_SUMMARY;

    public final static String JOB_ORDER_STATUS = "job_order_status";
    public final static int VIEW_JOB_ORDER = 1;
    public final static int RECEIVED_IN_MAIN = 2;

    public final static int REQUEST_UPDATE_STATUS = 302;
    public final static int REQUEST_SEARCH_JOB_ORDER = 303;

    JobOrders jobOrders;
    int job_order_status = VIEW_JOB_ORDER;
    EditText et_search;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_job_order);

        Intent in = getIntent();
        job_order_status = in.getExtras().getInt(JOB_ORDER_STATUS,VIEW_JOB_ORDER);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(job_order_status == VIEW_JOB_ORDER){
            String job_order_id = getIntent().getExtras().getString(JobOrders.JOB_ORDER_ID, "");
            jobOrders = realm.copyFromRealm(realm.where(JobOrders.class).equalTo("id", job_order_id).findFirst());
        }else if(job_order_status == RECEIVED_IN_MAIN){
            setTitle("Receive");
        }
        initUI();
    }
    public int getCurrentJobOrderStatus(){
        return job_order_status;
    }
    public void initUI(){
        jobOrderSummaryFragment = new JobOrderSummaryFragment();
        jobOrderSummaryFragment.setJobOrder(jobOrders);
        setFragment(jobOrderSummaryFragment,0,current_fragment,false);

        et_search = (EditText) findViewById(R.id.et_search);
        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_UNSPECIFIED){
                    get_job_order_by_id(et_search.getText().toString());
                    return true;
                }
                return false;
            }
        });
        et_search.setVisibility( (job_order_status == RECEIVED_IN_MAIN) ? View.VISIBLE : View.GONE);
    }
    public void setReceiver(String response,int process,int status){
        showProgress(false);
        switch (process){
            case REQUEST_UPDATE_STATUS:
                if(setJobOrder(response)){
                    Toast.makeText(this, "Received Success", Toast.LENGTH_SHORT).show();
                    Utils.syncImages(this);
                    switchFragment(RESTART_ACTIVITY);
                }
                break;
            case REQUEST_SEARCH_JOB_ORDER:
                if(setSearchJobOrder(response)){

                }
                break;
        }
    }

    public AppCompatActivity getActivity(){
        return this;
    }

    @Override
    public void onBackPressed(){
        if(current_fragment == FRAGMENT_JOB_ORDER_SUMMARY){
            switchFragment(CLOSE_JOB_ORDER);
        }else if(current_fragment == FRAGMENT_JOB_ORDER_VIEW_IMGAGES){
            switchFragment(FRAGMENT_JOB_ORDER_SUMMARY);
        }
    }

    JobOrderSummaryFragment jobOrderSummaryFragment;
    public void switchFragment(int fragment){
        if(current_fragment == fragment){
            return;
        }
        int before_fragment = current_fragment;
        current_fragment = fragment;
        switch (fragment){
            case FRAGMENT_JOB_ORDER_SUMMARY:
                jobOrderSummaryFragment = new JobOrderSummaryFragment();
                jobOrderSummaryFragment.setJobOrder(jobOrders);
                setFragment(jobOrderSummaryFragment,before_fragment,current_fragment);
                break;
            case CLOSE_JOB_ORDER:
                finish();
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            case FRAGMENT_JOB_ORDER_VIEW_IMGAGES:
                if(jobOrders != null){
                    ImageViewerFragment imageViewerFragment = new ImageViewerFragment();
                    imageViewerFragment.setJobOrder(jobOrders);
                    setFragment(imageViewerFragment,before_fragment,current_fragment);
                }
                break;
            case RESTART_ACTIVITY:
                Intent in = new Intent(ViewJobOrderActivity.this,ViewJobOrderActivity.class);
                in.putExtra(ViewJobOrderActivity.JOB_ORDER_STATUS,ViewJobOrderActivity.RECEIVED_IN_MAIN);
                startActivity(in);
                finish();
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
                break;
        }
    }

    public void setFragment(Fragment fragment,int before, int after){
        setFragment(fragment,before,after,true);
    }
    public void setFragment(Fragment fragment, int before, int after, boolean no_animation){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if(no_animation){
            if(before > after){
                fragmentTransaction.setCustomAnimations(
                        R.anim.right_in,
                        R.anim.left_out);
            }else{
                fragmentTransaction.setCustomAnimations(
                        R.anim.left_in,
                        R.anim.right_out);
            }
        }
        fragmentTransaction.replace(R.id.fragment_container, fragment).commit();
    }

    public void save(){
        switch (jobOrders.getStatus_id()){
            case JobOrders.ACTION_FORWARDED:
                if(accounts.getAccount_type_id() == 2){
                    showProgress(true);
                    jobOrders.setStatus_id(JobOrders.ACTION_RECEIVE_AT_MAIN);
                    jobOrders.setRepair_status(1);
                    update_status();
                }
                break;
            case JobOrders.ACTION_FOR_RETURN:
                if(accounts.getAccount_type_id() == 1){
                    showProgress(true);
                    jobOrders.setStatus_id(JobOrders.ACTION_RECEIVE_AT_SC);
                    update_status();
                }
                break;
        }
    }
    public void update_status(){
        requestManager.setRequestAsync(requestManager.getApiService().update_job_order_receive_status(jobOrders.getId(),jobOrders.getStatus_id(),jobOrders.getRepair_status()),REQUEST_UPDATE_STATUS);
    }

    public boolean setJobOrder(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            if(jsonObject.getInt(RetrofitRequestManager.SUCCESS) == 1){
                JSONArray jsonArray = jsonObject.getJSONArray(JobOrders.TABLE_NAME);
                final ArrayList<JobOrders> jobOrders = new GsonBuilder()
                        .registerTypeAdapter(JobOrders.class,new JobOrderSerialize())
                        .setDateFormat("yyyy-MM-dd HH:mm:ss").create()
                        .fromJson(jsonArray.toString(), new TypeToken<List<JobOrders>>(){}.getType());
                if(!jobOrders.isEmpty()){
                    realm.executeTransaction(new Realm.Transaction(){
                        @Override
                        public void execute(Realm realm){
                            jobOrders.get(0).getJobOrderImages().addAll(ViewJobOrderActivity.this.jobOrders.getJobOrderImages());
                            realm.copyToRealmOrUpdate(jobOrders.get(0));
                        }
                    });
                }else{
                    errorMessage("Job Order does not exist");
                    return false;
                }
            }else{
                errorMessage(jsonObject.getString(RetrofitRequestManager.MESSAGE));
                return false;
            }
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void get_job_order_by_id(String s){
        requestManager.setRequestAsync(requestManager.getApiService().get_job_order_by_id(s),REQUEST_SEARCH_JOB_ORDER);
    }
    public boolean setSearchJobOrder(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            if(jsonObject.getInt(RetrofitRequestManager.SUCCESS) == 1){
                JSONArray jsonArray = jsonObject.getJSONArray(JobOrders.TABLE_NAME);
                final ArrayList<JobOrders> jobOrders = new GsonBuilder()
                        .registerTypeAdapter(JobOrders.class,new JobOrderSerialize())
                        .setDateFormat("yyyy-MM-dd HH:mm:ss").create()
                        .fromJson(jsonArray.toString(), new TypeToken<List<JobOrders>>(){}.getType());
                if(!jobOrders.isEmpty()){
                    realm.executeTransaction(new Realm.Transaction(){
                        @Override
                        public void execute(Realm realm){
                            RealmResults<JobOrderImages> images = realm.where(JobOrderImages.class).lessThan("id",0).equalTo("job_order_id",jobOrders.get(0).getId()).findAll();
                            jobOrders.get(0).getJobOrderImages().addAll(images);
                            evaluateReceived(realm.copyToRealmOrUpdate(jobOrders.get(0)));
                        }
                    });
                }else{
                    errorMessage("Job Order does not exist");
                    return false;
                }
            }else{
                errorMessage(jsonObject.getString(RetrofitRequestManager.MESSAGE));
                return false;
            }
        }catch (JSONException e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    public void evaluateReceived(JobOrders jobOrder){
        if(jobOrder != null){
            if(accounts.getAccount_type_id() == 1){
                if(jobOrder.getStatus_id() == JobOrders.ACTION_FOR_RETURN){
                    jobOrders = realm.copyFromRealm(jobOrder);
                    jobOrderSummaryFragment.setJobOrder(jobOrders);
                    jobOrderSummaryFragment.setValues();
                }else if(jobOrder.getStatus_id() == JobOrders.ACTION_RECEIVE_AT_SC){
                    Utils.errorMessage(ViewJobOrderActivity.this, "Is Already Received At SC", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                }else{
                    Utils.errorMessage(ViewJobOrderActivity.this, "Not in a For Return status", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i){

                        }
                    });
                }
            }else if(accounts.getAccount_type_id() == 2){
                if(jobOrder.getStatus_id() == JobOrders.ACTION_FORWARDED){
                    jobOrders = realm.copyFromRealm(jobOrder);
                    jobOrderSummaryFragment.setJobOrder(jobOrders);
                    jobOrderSummaryFragment.setValues();
                }else if(jobOrder.getStatus_id() == JobOrders.ACTION_RECEIVE_AT_MAIN){
                    Utils.errorMessage(ViewJobOrderActivity.this, "Is Already Received", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                }else{
                    Utils.errorMessage(ViewJobOrderActivity.this, "Not in a FORWARDED status", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i){

                        }
                    });
                }
            }
        }else{
            Utils.errorMessage(ViewJobOrderActivity.this, "Not Existing Job Order Id", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i){

                }
            });
        }
    }
}
