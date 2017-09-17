package com.fusiotec.servicecenterapi.servicecenter.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.fusiotec.servicecenterapi.servicecenter.R;
import com.fusiotec.servicecenterapi.servicecenter.fragments.CustomerInfoFragment;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.Customers;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.Stations;
import com.fusiotec.servicecenterapi.servicecenter.network.RetrofitRequestManager;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

/**
 * Created by Owner on 9/3/2017.
 */

public class CustomerInfoActivity extends BaseActivity implements
        CustomerInfoFragment.CustomerInfoFragmentListener {

    public final static int FRAGMENT_ADD_CUSTOMER_INFO = 1;
    int current_fragment = FRAGMENT_ADD_CUSTOMER_INFO;

    public final static String SHOW_FRAGMENT = "show_fragment";
    public final static String SHOW_CUSTOMER_ID = "show_customer_id";

    public final static int REQUEST_CREATE_CUSTOMERS = 302;
    public final static int REQUEST_UPDATE_CUSTOMERS = 303;
    public final static int REQUEST_DELETE_CUSTOMERS = 304;
    boolean isUpdate;
    Customers selected_customers;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_template);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        int customer_id = getIntent().getIntExtra(SHOW_CUSTOMER_ID,0);
        isUpdate = customer_id != 0;
        if(!isUpdate){
            selected_customers = new Customers();
        }else{
            selected_customers = realm.copyFromRealm(realm.where(Customers.class).equalTo("id",customer_id).findFirst());
        }

        current_fragment = getIntent().getExtras().getInt(SHOW_FRAGMENT,current_fragment);

        switch (current_fragment){
            case FRAGMENT_ADD_CUSTOMER_INFO:
                CustomerInfoFragment customerInfoFragment = new CustomerInfoFragment();
                customerInfoFragment.setCustomer(selected_customers);
                customerInfoFragment.setStatus(isUpdate ? CustomerInfoFragment.UPDATE_CUSTOMER : CustomerInfoFragment.NEWLY_CREATED_CUSTOMER);
                setFragment(customerInfoFragment,0,current_fragment,false);
                break;
        }
    }
    public void setReceiver(String response,int process,int status){
        showProgress(false);
        switch (process){
            case REQUEST_CREATE_CUSTOMERS:
                if(setCustomer(response)){
                    Toast.makeText(this, "Successfully Added!", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
                break;
            case REQUEST_UPDATE_CUSTOMERS:
                if(setCustomer(response)){
                    Toast.makeText(this, "Update Success!", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
                break;
            case REQUEST_DELETE_CUSTOMERS:
                if(setCustomer(response)){
                    Toast.makeText(this, "Delete Success!", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
                break;
        }
    }
    public void switchFragment(int fragment){
        if(current_fragment == fragment){
            return;
        }
        int before_fragment = current_fragment;
        current_fragment = fragment;
        switch (fragment){
            case FRAGMENT_ADD_CUSTOMER_INFO:
                CustomerInfoFragment customerInfoFragment = new CustomerInfoFragment();
                customerInfoFragment.setCustomer(selected_customers);
                setFragment(customerInfoFragment,before_fragment,current_fragment);
                break;
        }
    }
    public void setFragment(Fragment fragment, int before, int after){
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
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_delete:
                new AlertDialog.Builder(this)
                        .setTitle("Confirmation")
                        .setMessage("Are sure you want to delete this Branch?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                selected_customers.setIs_deleted(1);
                                delete_customer();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        if(isUpdate){
            getMenuInflater().inflate(R.menu.action_delete, menu);
        }
        return true;
    }

    @Override
    public void onBackPressed(){
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public void create_customer_info(){
        showProgress(true);
        requestManager.setRequestAsync(requestManager.getApiService().create_customers(selected_customers.getFirst_name(),selected_customers.getLast_name(),selected_customers.getAddress(),selected_customers.getPhone_no(),selected_customers.getEmail(),selected_customers.getAccount_id(),selected_customers.getStation_id()),REQUEST_CREATE_CUSTOMERS);
    }
    public void update_customers(){
        showProgress(true);
        requestManager.setRequestAsync(requestManager.getApiService().update_customers(selected_customers.getId(),selected_customers.getFirst_name(),selected_customers.getLast_name(),selected_customers.getAddress(),selected_customers.getPhone_no(),selected_customers.getEmail(),selected_customers.getAccount_id()),REQUEST_UPDATE_CUSTOMERS);
    }
    public void delete_customer(){
        showProgress(true);
        requestManager.setRequestAsync(requestManager.getApiService().delete_customer(selected_customers.getId()),REQUEST_DELETE_CUSTOMERS);
    }
    public void save(){
        if(selected_customers.getId() > 0){
            update_customers();
        }else{
            create_customer_info();
        }
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
}
