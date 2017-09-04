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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.claudiodegio.msv.OnSearchViewListener;
import com.fusiotec.servicecenterapi.servicecenter.R;
import com.fusiotec.servicecenterapi.servicecenter.customviews.CustomSuggestionSearch;
import com.fusiotec.servicecenterapi.servicecenter.fragments.ImageViewerFragment;
import com.fusiotec.servicecenterapi.servicecenter.fragments.JobOrderSummaryFragment;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.JobOrders;
import com.fusiotec.servicecenterapi.servicecenter.utilities.Utils;

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

    JobOrders jobOrders;
    int job_order_status = VIEW_JOB_ORDER;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_template);


        Intent in = getIntent();
        job_order_status = in.getExtras().getInt(JOB_ORDER_STATUS,VIEW_JOB_ORDER);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(job_order_status == VIEW_JOB_ORDER) {
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

    CustomSuggestionSearch searchView;
    public void initUI(){
        jobOrderSummaryFragment = new JobOrderSummaryFragment();
        jobOrderSummaryFragment.setJobOrder(jobOrders);
        setFragment(jobOrderSummaryFragment,0,current_fragment,false);

        searchView = (CustomSuggestionSearch) findViewById(R.id.sv);
        searchView.setOnSearchViewListener(new OnSearchViewListener() {
            @Override
            public void onSearchViewShown(){

            }

            @Override
            public void onSearchViewClosed(){
            }

            @Override
            public boolean onQueryTextSubmit(String s){
                JobOrders jobOrder = realm.where(JobOrders.class).equalTo("id",s).findFirst();
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
                        }
                    }else if(accounts.getAccount_type_id() == 2) {
                        if(jobOrder.getStatus_id() == JobOrders.ACTION_FORWARDED){
                            jobOrders = realm.copyFromRealm(jobOrder);
                            jobOrderSummaryFragment.setJobOrder(jobOrders);
                            jobOrderSummaryFragment.setValues();
                        }else if(jobOrder.getStatus_id() == JobOrders.ACTION_RECEIVE_AT_MAIN) {
                            Utils.errorMessage(ViewJobOrderActivity.this, "Is Already Received", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                        }else {
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
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                }
                return true;
            }

            @Override
            public void onQueryTextChange(String s){

            }
        });
    }
    MenuItem item;
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.action_search, menu);

        item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        if(job_order_status == RECEIVED_IN_MAIN){
            item.setVisible(true);
        }else{
            item.setVisible(false);
        }
        return true;
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

}
