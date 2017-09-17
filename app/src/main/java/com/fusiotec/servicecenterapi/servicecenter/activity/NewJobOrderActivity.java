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
import com.fusiotec.servicecenterapi.servicecenter.models.serialize_object.JobOrderSerialize;
import com.fusiotec.servicecenterapi.servicecenter.network.RetrofitRequestManager;
import com.fusiotec.servicecenterapi.servicecenter.utilities.Utils;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;


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

    public final static int REQUEST_CREATE_CUSTOMERS = 302;
    public final static int REQUEST_CREATE_JOB_ORDERS = 303;

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
        for(Customers temp:realm.where(Customers.class).equalTo("is_deleted",0).findAll()){
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
            public boolean onQueryTextSubmit(String s){
                Toast.makeText(NewJobOrderActivity.this, s, Toast.LENGTH_SHORT).show();
                String[] st = s.split(", ");
                if(st.length == 2){
                    Customers customer = realm.where(Customers.class).equalTo("last_name",st[0]).equalTo("first_name",st[1]).findFirst();
                    if(customer != null){
                        jobOrders.setCustomer(realm.copyFromRealm(customer));
                        customerInfoFragment.setCustomer(jobOrders.getCustomer());
                        customerInfoFragment.fieldIsEditable(false);
                        customerInfoFragment.setValues();
                    }
                }
                return true;
            }

            @Override
            public void onQueryTextChange(String s){

            }
        });

        customerInfoFragment = new CustomerInfoFragment();
        customerInfoFragment.setJobOrders(jobOrders);
        customerInfoFragment.setCustomer(jobOrders.getCustomer());
        setFragment(customerInfoFragment,0,current_fragment,false);
    }

    public void setReceiver(String response,int process,int status) {
        switch (process){
            case REQUEST_CREATE_CUSTOMERS:
                setCustomer(response);
                break;
            case REQUEST_CREATE_JOB_ORDERS:
                showProgress(false);
                if(setJobOrder(response)){
                    Toast.makeText(this, "Successful Added!", Toast.LENGTH_SHORT).show();
                    Utils.syncImages(this);
                    switchFragment(CLOSE_JOB_ORDER);
                }
                break;
        }
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
                customerInfoFragment.setJobOrders(jobOrders);
                customerInfoFragment.setCustomer(jobOrders.getCustomer());
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

    public void save(){
        showProgress(true);
        if(jobOrders.getCustomer().getId() == 0){
            create_customer_info(jobOrders.getCustomer());
        }else{
            createJobOrder(jobOrders);
        }
    }
    public void create_customer_info(Customers selected_customers){
        requestManager.setRequestAsync(requestManager.getApiService().create_customers(selected_customers.getFirst_name(),selected_customers.getLast_name(),selected_customers.getAddress(),selected_customers.getPhone_no(),selected_customers.getEmail(),selected_customers.getAccount_id(),selected_customers.getStation_id()),REQUEST_CREATE_CUSTOMERS);
    }
    public void createJobOrder(JobOrders jobOrders){
        requestManager.setRequestAsync(requestManager.getApiService().create_job_order(jobOrders.getId(),
                jobOrders.getUnit(),
                jobOrders.getModel(),
                Utils.dateToString(jobOrders.getDate_of_purchased(),"yyyy-mm-dd HH:mm:ss"),
                jobOrders.getDealer(),
                jobOrders.getSerial_number(),
                jobOrders.getWarranty_label(),
                jobOrders.getComplaint(),
                jobOrders.getCustomer_id(),
                jobOrders.getAccount_id(),
                jobOrders.getStatus_id(),
                jobOrders.getStation_id()),REQUEST_CREATE_JOB_ORDERS);
    }
    public boolean setCustomer(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            if(jsonObject.getInt(RetrofitRequestManager.SUCCESS) == 1){
                JSONArray jsonArray = jsonObject.getJSONArray(Customers.TABLE_NAME);
                final ArrayList<Customers> customers = new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd HH:mm:ss").create()
                        .fromJson(jsonArray.toString(), new TypeToken<List<Customers>>(){}.getType());
                if(!customers.isEmpty()){
                    realm.executeTransaction(new Realm.Transaction(){
                        @Override
                        public void execute(Realm realm){
                            realm.copyToRealmOrUpdate(customers.get(0));
                        }
                    });
                    jobOrders.setCustomer(customers.get(0));
                    jobOrders.setCustomer_id(customers.get(0).getId());
                    createJobOrder(jobOrders);
                }else{
                    errorMessage("Customer does not exist");
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
                            NewJobOrderActivity.this.jobOrders.getJobOrderImageslist().remove(NewJobOrderActivity.this.jobOrders.getJobOrderImageslist().size()-1);
                            jobOrders.get(0).getJobOrderImages().addAll(NewJobOrderActivity.this.jobOrders.getJobOrderImages());
                            jobOrders.get(0).getJobOrderImages().addAll(NewJobOrderActivity.this.jobOrders.getJobOrderImageslist());
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
