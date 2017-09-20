package com.fusiotec.servicecenterapi.servicecenter.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.fusiotec.servicecenterapi.servicecenter.R;
import com.fusiotec.servicecenterapi.servicecenter.adapters.JobOrderImagesAdapter;
import com.fusiotec.servicecenterapi.servicecenter.fragments.ImageViewerFragment;
import com.fusiotec.servicecenterapi.servicecenter.fragments.JobOrderImagesFragment;
import com.fusiotec.servicecenterapi.servicecenter.fragments.JobOrderSummaryFragment;
import com.fusiotec.servicecenterapi.servicecenter.fragments.RepairStatusFragment;
import com.fusiotec.servicecenterapi.servicecenter.fragments.ShippingFragment;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.JobOrderImages;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.JobOrderRepairStatus;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.JobOrderShipping;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.JobOrders;
import com.fusiotec.servicecenterapi.servicecenter.models.serialize_object.JobOrderSerialize;
import com.fusiotec.servicecenterapi.servicecenter.network.RetrofitRequestManager;
import com.fusiotec.servicecenterapi.servicecenter.utilities.Utils;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Owner on 8/13/2017.
 */

public class RepairStatusActivity extends BaseActivity implements
        JobOrderImagesFragment.JobOrderImagesFragmentListener,
        JobOrderImagesAdapter.JobOrderImagesAdapterListener,
        RepairStatusFragment.RepairStatusFragmentListener,
        JobOrderSummaryFragment.JobOrderSummaryFragmentListener,
        ImageViewerFragment.ImageViewerActivityListener{

    public final static int FRAGMENT_JOB_ORDER_SUMMARY = 1;
    public final static int FRAGMENT_JOB_ORDER_REPAIR_STATUS = 2;
    public final static int FRAGMENT_JOB_ORDER_IMAGES = 3;
    public final static int CLOSE_JOB_ORDER = 4;
    public final static int FRAGMENT_JOB_ORDER_VIEW_IMGAGES = 5;

    public final static int REQUEST_REPAIR_STATUS = 301;

    int current_fragment = FRAGMENT_JOB_ORDER_SUMMARY;

    JobOrders jobOrders;
    JobOrderImagesFragment jobOrderImagesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_template);

        String job_order_id = getIntent().getExtras().getString(JobOrders.JOB_ORDER_ID,"");
        jobOrders = realm.copyFromRealm(realm.where(JobOrders.class).equalTo("id",job_order_id).findFirst());
        jobOrders.setJobOrderRepairStatus(new JobOrderRepairStatus());
        jobOrders.getJobOrderRepairStatus().setJob_order_id(jobOrders.getId());
        jobOrders.getJobOrderImageslist().add(new JobOrderImages());
        getMaxImage = Utils.getMax(realm,JobOrderImages.class,"id");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initUI();
    }
    public void setReceiver(String response,int process,int status){
        showProgress(false);
        switch (process){
            case REQUEST_REPAIR_STATUS:
                if(setJobOrder(response)){
                    Toast.makeText(this, "Update Success", Toast.LENGTH_SHORT).show();
                    switchFragment(CLOSE_JOB_ORDER);
                }
                break;
        }
    }
    public int getCurrentJobOrderStatus(){
        return 0;
    }
    public void initUI(){
        JobOrderSummaryFragment jobOrderSummaryFragment = new JobOrderSummaryFragment();
        jobOrderSummaryFragment.setJobOrder(jobOrders);
        setFragment(jobOrderSummaryFragment,0,current_fragment,false);
    }

    public AppCompatActivity getActivity(){
        return this;
    }

    @Override
    public void onBackPressed(){
        if(current_fragment == FRAGMENT_JOB_ORDER_SUMMARY){
            switch (jobOrders.getStatus_id()){
                case JobOrders.ACTION_RECEIVE_AT_MAIN:
                    if(jobOrders.getRepair_status() == 1){
                        switchFragment(CLOSE_JOB_ORDER);
                    }else{
                        switchFragment(FRAGMENT_JOB_ORDER_IMAGES);
                    }
                    break;
           }
        }else if(current_fragment == FRAGMENT_JOB_ORDER_REPAIR_STATUS){
            alertToExit();
        }else if(current_fragment == FRAGMENT_JOB_ORDER_IMAGES){
            switchFragment(FRAGMENT_JOB_ORDER_REPAIR_STATUS);
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

    Uri outputFileUri;
    Uri fileUri;
    String image_cont;
    JobOrderImages selected_jobOrderImage;

    @Override
    public void addImage(JobOrderImages jobOrderImage){
        this.selected_jobOrderImage = jobOrderImage;
        outputFileUri = Utils.openImageIntent(this);
    }

    public void switchFragment(int fragment){
        if(current_fragment == fragment){
            return;
        }
        int before_fragment = current_fragment;
        current_fragment = fragment;
        switch (fragment){
            case FRAGMENT_JOB_ORDER_REPAIR_STATUS:
                RepairStatusFragment repairStatusFragment = new RepairStatusFragment();
                repairStatusFragment.setJobOrder(jobOrders);
                setFragment(repairStatusFragment,before_fragment,current_fragment);
                break;
            case FRAGMENT_JOB_ORDER_IMAGES:
                jobOrderImagesFragment = new JobOrderImagesFragment();
                jobOrderImagesFragment.setJobOrder(jobOrders);
                setFragment(jobOrderImagesFragment,before_fragment,current_fragment);
                break;
            case FRAGMENT_JOB_ORDER_SUMMARY:
                JobOrderSummaryFragment jobOrderSummaryFragment = new JobOrderSummaryFragment();
                jobOrderSummaryFragment.setJobOrder(jobOrders);
                setFragment(jobOrderSummaryFragment,before_fragment,current_fragment);
                break;
            case CLOSE_JOB_ORDER:
                finish();
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            case FRAGMENT_JOB_ORDER_VIEW_IMGAGES:
                ImageViewerFragment imageViewerFragment = new ImageViewerFragment();
                imageViewerFragment.setJobOrder(jobOrders);
                setFragment(imageViewerFragment,before_fragment,current_fragment);
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
                        Utils.getResizeImage(image_cont);
                        setJobOrderImage(selected_jobOrderImage,image_cont);
                        jobOrderImagesFragment.refreshImageList();
                    } else {
                        fileUri = data == null ? null : data.getData();
                        if(fileUri != null) {
                            image_cont = Utils.getPath(fileUri, getActivity());
                            Utils.getResizeImage(image_cont);
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
        jobOrderImage.setJob_order_status_id(JobOrders.ACTION_RECEIVE_AT_MAIN);
    }
    public void save(){
        showProgress(true);
        repair_status();
    }
    public void repair_status(){
        requestManager.setRequestAsync(requestManager.getApiService().create_job_order_repair_status(jobOrders.getId(),jobOrders.getJobOrderRepairStatus().getRepair_note(),jobOrders.getJobOrderRepairStatus().getRepair_status(),jobOrders.getJobOrderRepairStatus().getAccount_id()),REQUEST_REPAIR_STATUS);
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
                            RepairStatusActivity.this.jobOrders.getJobOrderImageslist().remove(RepairStatusActivity.this.jobOrders.getJobOrderImageslist().size()-1);
                            jobOrders.get(0).getJobOrderImages().addAll(RepairStatusActivity.this.jobOrders.getJobOrderImageslist());

                            RealmResults<JobOrderImages> images = realm.where(JobOrderImages.class).lessThan("id",0).equalTo("job_order_id",jobOrders.get(0).getId()).findAll();
                            jobOrders.get(0).getJobOrderImages().addAll(images);
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
}
