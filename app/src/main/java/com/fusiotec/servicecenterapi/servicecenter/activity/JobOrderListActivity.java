package com.fusiotec.servicecenterapi.servicecenter.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fusiotec.servicecenterapi.servicecenter.R;
import com.fusiotec.servicecenterapi.servicecenter.adapters.JobOrdersAdapter;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.Customers;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.JobOrderImages;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.JobOrderStatus;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.JobOrders;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.Stations;
import com.fusiotec.servicecenterapi.servicecenter.models.serialize_object.JobOrderSerialize;
import com.fusiotec.servicecenterapi.servicecenter.network.RetrofitRequestManager;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by Owner on 8/13/2017.
 */

public class JobOrderListActivity extends BaseActivity{
    RecyclerView rv_list;
    RealmResults<JobOrders> jobOrders;
    EditText et_search;
    AppCompatSpinner sp_filter;
    JobOrdersAdapter jobOrdersAdapter;
    SwipeRefreshLayout swipeContainer;

    final public static int SHOW_OPEN_JOB_ORDERS = 1;
    final public static int SHOW_HISTORY = 2;
    final public static int SHOW_JOB_ORDERS_BY_CUSTOMER = 3;

    final public static int REQUEST_GET_JOB_ORDERS = 302;

    final public static String SHOW = "show";
    final public static String CUSTOMER_ID = "customer_id";

    int show_type = SHOW_OPEN_JOB_ORDERS;
    Customers selected_customer;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_order_list);

        show_type = getIntent().getExtras().getInt(SHOW, SHOW_OPEN_JOB_ORDERS);
        if(show_type == SHOW_JOB_ORDERS_BY_CUSTOMER){
            selected_customer = realm.where(Customers.class)
                    .equalTo("id",getIntent().getExtras().getInt(CUSTOMER_ID, 0))
                    .findFirst();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initUI();
    }
    @Override
    public void showProgress(boolean show){
        if(show){
            if(swipeContainer != null){
                if(!swipeContainer.isRefreshing()){
                    swipeContainer.setRefreshing(true);
                }
            }
        }else{
            if(swipeContainer != null){
                if(swipeContainer.isRefreshing()){
                    swipeContainer.setRefreshing(false);
                }
            }
        }
    }
    public void setReceiver(String response,int process,int status){
        showProgress(false);
        switch (process){
            case REQUEST_GET_JOB_ORDERS:
                setJobOrder(response);
                break;
        }
    }
    public RealmQuery<JobOrders> getStartingQuery(){
        switch (show_type){
            case SHOW_OPEN_JOB_ORDERS:
                return realm.where(JobOrders.class)
                        .notEqualTo("status_id",JobOrders.ACTION_CLOSED);
            case SHOW_HISTORY:
                return realm.where(JobOrders.class)
                        .equalTo("status_id",JobOrders.ACTION_CLOSED);
            case SHOW_JOB_ORDERS_BY_CUSTOMER:
                return realm.where(JobOrders.class)
                        .equalTo("customer_id",selected_customer.getId());
        }
        return null;
    }
    public void initUI(){
        switch (show_type){
            case SHOW_OPEN_JOB_ORDERS:
                jobOrders = getStartingQuery().findAllSorted("date_created", Sort.DESCENDING);
                break;
            case SHOW_HISTORY:
                jobOrders = getStartingQuery().findAllSorted("date_created", Sort.DESCENDING);
                break;
            case SHOW_JOB_ORDERS_BY_CUSTOMER:
                jobOrders = getStartingQuery().findAllSorted("date_created", Sort.DESCENDING);
                break;
        }

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh(){
                getJobOrders(et_search.getText().toString());
            }
        });

        rv_list = (RecyclerView) findViewById(R.id.rv_list);
        rv_list.setLayoutManager(new LinearLayoutManager(this));
        jobOrdersAdapter = new JobOrdersAdapter(this,jobOrders,realm.where(Stations.class).findAll());
        jobOrdersAdapter.setAccounts(accounts);
        rv_list.setAdapter(jobOrdersAdapter);

        et_search = (EditText) findViewById(R.id.et_search);
        sp_filter = (AppCompatSpinner) findViewById(R.id.sp_filter);
        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_UNSPECIFIED){
                    Toast.makeText(JobOrderListActivity.this, et_search.getText().toString(), Toast.LENGTH_SHORT).show();
                    search(et_search.getText().toString(),sp_filter.getSelectedItemPosition());
                    return true;
                }
                return false;
            }
        });
    }
    public void search(String search, int filter){
        switch (filter){
            case 0:
                jobOrders = getStartingQuery()
                        .contains("id",search, Case.INSENSITIVE)
                        .findAllSorted("date_created", Sort.DESCENDING);
                break;
            case 1:
                RealmResults<JobOrderStatus> jobOrderStatus = realm.where(JobOrderStatus.class)
                        .contains("name",search, Case.INSENSITIVE).findAll();

                if(jobOrderStatus.isEmpty()){
                    jobOrders = getStartingQuery().equalTo("status_id",0).findAll();
                }else{
                    RealmQuery<JobOrders> jquery = getStartingQuery();
                    jquery.beginGroup();
                    jquery.equalTo("status_id",jobOrderStatus.get(0).getId());
                    for (int i = 1; i < jobOrderStatus.size(); i++){
                        jquery.or();
                        jquery.equalTo("status_id",jobOrderStatus.get(i).getId());
                    }
                    jquery.endGroup();
                    jobOrders = jquery.findAllSorted("date_created", Sort.DESCENDING);
                }
                break;
        }
        jobOrdersAdapter.setData(jobOrders);
        jobOrdersAdapter.notifyDataSetChanged();
        getJobOrders(et_search.getText().toString());
    }

    @Override
    public void onBackPressed(){
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public void getJobOrders(String s){
        switch (show_type){
            case SHOW_OPEN_JOB_ORDERS:
                requestManager.setRequestAsync(requestManager.getApiService().get_job_orders(show_type,0,(accounts.isAdmin() ? 0 : (accounts.isMainBranch() ? 0 : accounts.getStation_id())),s),REQUEST_GET_JOB_ORDERS);
                break;
            case SHOW_HISTORY:
                requestManager.setRequestAsync(requestManager.getApiService().get_job_orders(show_type,0,(accounts.isAdmin() ? 0 : (accounts.isMainBranch() ? 0 : accounts.getStation_id())),s),REQUEST_GET_JOB_ORDERS);
                break;
            case SHOW_JOB_ORDERS_BY_CUSTOMER:
                requestManager.setRequestAsync(requestManager.getApiService().get_job_orders(show_type,selected_customer.getId(),(accounts.isAdmin() ? 0 : (accounts.isMainBranch() ? 0 : accounts.getStation_id())),s),REQUEST_GET_JOB_ORDERS);
                break;
        }
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
                            for(JobOrders temp:jobOrders){
                                RealmResults<JobOrderImages> images = realm.where(JobOrderImages.class).lessThan("id",0).equalTo("job_order_id",temp.getId()).findAll();
                                temp.getJobOrderImages().addAll(images);
                                realm.copyToRealmOrUpdate(temp);
                            }
                        }
                    });
                }else{

                    return false;
                }
            }else{

                return false;
            }
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
