package com.fusiotec.servicecenterapi.servicecenter.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.claudiodegio.msv.OnSearchViewListener;
import com.fusiotec.servicecenterapi.servicecenter.R;
import com.fusiotec.servicecenterapi.servicecenter.adapters.JobOrderImagesAdapter;
import com.fusiotec.servicecenterapi.servicecenter.customviews.CustomSuggestionSearch;
import com.fusiotec.servicecenterapi.servicecenter.fragments.CustomerInfoFragment;
import com.fusiotec.servicecenterapi.servicecenter.fragments.ImageViewerFragment;
import com.fusiotec.servicecenterapi.servicecenter.fragments.JobOrderImagesFragment;
import com.fusiotec.servicecenterapi.servicecenter.fragments.JobOrderSummaryFragment;
import com.fusiotec.servicecenterapi.servicecenter.fragments.NewJobOrderFragment;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.Accounts;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.Customers;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.JobOrderImages;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.JobOrders;
import com.fusiotec.servicecenterapi.servicecenter.utilities.Utils;

import java.util.ArrayList;
import java.util.List;


public class NewJobOrderActivity extends BaseActivity implements
    CustomerInfoFragment.CustomerInfoFragmentListener,
    NewJobOrderFragment.NewJobOrderFragmentListener,
    JobOrderSummaryFragment.JobOrderSummaryFragmentListener,
    JobOrderImagesFragment.JobOrderImagesFragmentListener,
    JobOrderImagesAdapter.JobOrderImagesAdapterListener,
    ImageViewerFragment.ImageViewerActivityListener{

    public final static int FRAGMENT_CUSTOMER_INFO = 1;
    public final static int FRAGMENT_NEW_JOB_ORDER = 2;
    public final static int FRAGMENT_JOB_ORDER_IMAGES = 3;
    public final static int FRAGMENT_JOB_ORDER_SUMMARY = 4;
    public final static int CLOSE_JOB_ORDER = 5;
    public final static int FRAGMENT_JOB_ORDER_VIEW_IMGAGES = 6;

    JobOrders jobOrders;
    int current_fragment = FRAGMENT_CUSTOMER_INFO;
    JobOrderImagesFragment jobOrderImagesFragment;
    CustomSuggestionSearch searchView;
    CustomerInfoFragment customerInfoFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_template);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        jobOrders = new JobOrders();
        jobOrders.setCustomer(new Customers());
        jobOrders.getJobOrderImageslist().add(new JobOrderImages());
        jobOrders.setId( accounts.getId() +  accounts.getStation().getStation_prefix() + Utils.dateToString(Utils.getServerDate(ls),"yyyyMMddHHmmss"));
        jobOrders.setStation_id(accounts.getStation().getId());
        jobOrders.setAccount_id(accounts.getId());
        jobOrders.setStatus_id(JobOrders.ACTION_PROCESSING);
        getMaxImage = Utils.getMax(realm,JobOrderImages.class,"id");

        List<String> customers = new ArrayList<>();
        for(Customers temp:realm.where(Customers.class).findAll()){
            customers.add(temp.getLast_name()+", "+temp.getFirst_name());
        }
        searchView = (CustomSuggestionSearch) findViewById(R.id.sv);
        searchView.setSuggestion(customers);
        searchView.setOnSearchViewListener(new OnSearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed(){
                Toast.makeText(NewJobOrderActivity.this, "closed", Toast.LENGTH_SHORT).show();
                jobOrders.setCustomer(new Customers());
                customerInfoFragment.fieldIsEditable(true);
                customerInfoFragment.setValues();
            }

            @Override
            public boolean onQueryTextSubmit(String s) {
                Toast.makeText(NewJobOrderActivity.this, s, Toast.LENGTH_SHORT).show();
                String[] st = s.split(", ");
                if(st.length == 2){
                    Customers customer = realm.where(Customers.class).equalTo("last_name",st[0]).equalTo("first_name",st[1]).findFirst();
                    if(customer != null){
                        jobOrders.setCustomer(realm.copyFromRealm(customer));
                        customerInfoFragment.fieldIsEditable(false);
                        customerInfoFragment.setValues();
                    }
                }
                return true;
            }

            @Override
            public void onQueryTextChange(String s) {

            }
        });

        customerInfoFragment = new CustomerInfoFragment();
        customerInfoFragment.setJobOrder(jobOrders);
        setFragment(customerInfoFragment,0,current_fragment,false);
    }
    public int getCurrentJobOrderStatus(){
        return 0;
    }
    MenuItem item;
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.action_search, menu);

        item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }

    public AppCompatActivity getActivity(){
        return this;
    }

    Uri outputFileUri;
    Uri fileUri;
    String image_cont;
    JobOrderImages selected_jobOrderImage;
    @Override
    public void addImage(JobOrderImages jobOrderImage){
        this.selected_jobOrderImage = jobOrderImage;
        outputFileUri = Utils.openImageIntent(this);
    }

    @Override
    public void onBackPressed() {
        if(current_fragment == FRAGMENT_CUSTOMER_INFO){
            alertToExit();
        }else if(current_fragment == FRAGMENT_NEW_JOB_ORDER){
            switchFragment(FRAGMENT_CUSTOMER_INFO);
        }else if(current_fragment == FRAGMENT_JOB_ORDER_SUMMARY){
            switchFragment(FRAGMENT_JOB_ORDER_IMAGES);
        }else if(current_fragment == FRAGMENT_JOB_ORDER_IMAGES){
            switchFragment(FRAGMENT_NEW_JOB_ORDER);
        }else if(current_fragment == FRAGMENT_JOB_ORDER_VIEW_IMGAGES){
            switchFragment(FRAGMENT_JOB_ORDER_SUMMARY);
        }
    }

    public void alertToExit(){
        new AlertDialog.Builder(getActivity())
                .setTitle("Confirmation")
                .setMessage("Are sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switchFragment(CLOSE_JOB_ORDER);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }

    public void switchFragment(int fragment){
        if(current_fragment == fragment){
            return;
        }
        int before_fragment = current_fragment;
        current_fragment = fragment;
        switch (fragment){
            case FRAGMENT_CUSTOMER_INFO:
                customerInfoFragment = new CustomerInfoFragment();
                customerInfoFragment.setJobOrder(jobOrders);
                setFragment(customerInfoFragment,before_fragment,current_fragment);
                searchView.setVisibility(View.VISIBLE);
                item.setVisible(true);
                break;
            case FRAGMENT_NEW_JOB_ORDER:
                NewJobOrderFragment newJobOrderFragment = new NewJobOrderFragment();
                newJobOrderFragment.setJobOrder(jobOrders);
                setFragment(newJobOrderFragment,before_fragment,current_fragment);
                searchView.setVisibility(View.GONE);
                item.setVisible(false);
                break;
            case FRAGMENT_JOB_ORDER_IMAGES:
                jobOrderImagesFragment = new JobOrderImagesFragment();
                jobOrderImagesFragment.setJobOrder(jobOrders);
                setFragment(jobOrderImagesFragment,before_fragment,current_fragment);
                searchView.setVisibility(View.GONE);
                item.setVisible(false);
                break;
            case FRAGMENT_JOB_ORDER_SUMMARY:
                JobOrderSummaryFragment jobOrderSummaryFragment = new JobOrderSummaryFragment();
                jobOrderSummaryFragment.setJobOrder(jobOrders);
                setFragment(jobOrderSummaryFragment,before_fragment,current_fragment);
                searchView.setVisibility(View.GONE);
                item.setVisible(false);
                break;
            case CLOSE_JOB_ORDER:
                finish();
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                break;
            case FRAGMENT_JOB_ORDER_VIEW_IMGAGES:
                searchView.setVisibility(View.GONE);
                ImageViewerFragment imageViewerFragment = new ImageViewerFragment();
                imageViewerFragment.setJobOrder(jobOrders);
                setFragment(imageViewerFragment,before_fragment,current_fragment);
                searchView.setVisibility(View.GONE);
                item.setVisible(false);
                break;
        }
    }

    public void setFragment(Fragment fragment,int before, int after){
        setFragment(fragment,before,after,true);
    }
    public void setFragment(Fragment fragment,int before, int after,boolean no_animation){
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

    @Override
    public void onActivityResult(int requestCode, int resultCode,final Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Utils.CAMERA_CAPTURE_IMAGE_REQUEST_CODE){
                Log.e("CAMERA_CAPTURE_IMAGE",""+requestCode);
                try {
                    final boolean isCamera;
                    if (data == null) {
                        isCamera = true;
                    } else {
                        final String action = data.getAction();
                        if (action == null) {
                            isCamera = false;
                        } else {
                            isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        }
                    }
                    if (isCamera) {
                        fileUri = outputFileUri;
                        image_cont = fileUri.getPath();
                        setJobOrderImage(selected_jobOrderImage,image_cont);
                        jobOrderImagesFragment.refreshImageList();
                    } else {
                        fileUri = data == null ? null : data.getData();
                        if(fileUri != null) {
                            image_cont = Utils.getPath(fileUri, getActivity());
                            setJobOrderImage(selected_jobOrderImage,image_cont);
                            jobOrderImagesFragment.refreshImageList();
                        }
                    }
                }catch (Exception e){
                    Toast.makeText(getActivity(), "Try Again", Toast.LENGTH_LONG).show();
            }
            }
        }
    }
    long getMaxImage = 0;
    public void setJobOrderImage(JobOrderImages jobOrderImage,String path){
        if(jobOrderImage.getId() == 0){
            getMaxImage--;
            jobOrderImage.setId((int)getMaxImage);
            jobOrderImagesFragment.addTempHolder();
        }
        jobOrderImage.setImage(path);
        jobOrderImage.setJob_order_id(jobOrders.getId());
        jobOrderImage.setDate_created(Utils.getServerDate(ls));
        jobOrderImage.setJob_order_status_id(JobOrders.ACTION_PROCESSING);
    }
}
