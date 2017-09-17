package com.fusiotec.servicecenterapi.servicecenter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fusiotec.servicecenterapi.servicecenter.R;
import com.fusiotec.servicecenterapi.servicecenter.adapters.CustomerListAdapter;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.Customers;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Owner on 9/3/2017.
 */

public class CustomerListActivity extends BaseActivity {

    CustomerListAdapter customerListAdapter;
    RealmResults<Customers> customer_list;
    EditText et_search;
    SwipeRefreshLayout swipeContainer;

    public final static int REQUEST_GET_CUSTOMERS = 301;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initList();
        initSearch();
    }

    @Override
    public void showProgress(boolean show){
        if(swipeContainer != null){
            if(swipeContainer.isRefreshing()){
                swipeContainer.setRefreshing(false);
            }
        }
    }
    public void setReceiver(String response,int process,int status) {
        showProgress(false);
        switch (process){
            case REQUEST_GET_CUSTOMERS:
                setCustomers(response);
                break;
        }
    }
    public void initList(){
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh(){
                get_customers();
            }
        });

        customer_list = realm.where(Customers.class).equalTo("is_deleted",0).findAll();
        customerListAdapter = new CustomerListAdapter(this,customer_list);
        recyclerView.setAdapter(customerListAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewCustomer();
            }
        });
    }

    public void addNewCustomer(){
        Intent in = new Intent(this,CustomerInfoActivity.class);
        in.putExtra(CustomerInfoActivity.SHOW_FRAGMENT,CustomerInfoActivity.FRAGMENT_ADD_CUSTOMER_INFO);
        startActivity(in);
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }
    public void initSearch(){
        et_search = (EditText) findViewById(R.id.et_search);
        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    search(et_search.getText().toString());
                    return true;
                }
                return false;
            }
        });
    }

    public void search(String s){
        customer_list = realm.where(Customers.class)
                .equalTo("is_deleted",0)
                .beginGroup()
                    .contains("last_name",s, Case.INSENSITIVE)
                    .or()
                    .contains("first_name",s, Case.INSENSITIVE)
                .endGroup().findAll();
        setList();
    }

    public void setList(){
        customerListAdapter.setData(customer_list);
        customerListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }
    public void get_customers(){
        requestManager.setRequestAsync(requestManager.getApiService().get_customers(accounts.isAdmin() ? 0 : (accounts.isMainBranch() ? 0 : accounts.getStation_id())),REQUEST_GET_CUSTOMERS);
    }
    public boolean setCustomers(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
//            if(jsonObject.getInt(RetrofitRequestManager.SUCCESS) == 1){
            JSONArray jsonArray = jsonObject.getJSONArray(Customers.TABLE_NAME);
            final ArrayList<Customers> customers = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss").create()
                    .fromJson(jsonArray.toString(), new TypeToken<List<Customers>>(){}.getType());
            if(!customers.isEmpty()){
                realm.executeTransaction(new Realm.Transaction(){
                    @Override
                    public void execute(Realm realm){
                        realm.copyToRealmOrUpdate(customers);
                    }
                });
            }
//                else{
//                    errorMessage("Account does not exist");
//                    return false;
//                }
//            }else{
//                errorMessage(jsonObject.getString(RetrofitRequestManager.MESSAGE));
//                return false;
//            }
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
