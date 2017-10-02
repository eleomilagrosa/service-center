package com.fusiotec.servicecenterapi.servicecenter.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fusiotec.servicecenterapi.servicecenter.R;
import com.fusiotec.servicecenterapi.servicecenter.adapters.JobOrdersAdapter;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.Customers;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.JobOrderDiagnosis;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.JobOrderForReturn;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.JobOrderImages;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.JobOrderRepairStatus;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.JobOrderShipping;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.JobOrderStatus;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.JobOrders;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.Stations;
import com.fusiotec.servicecenterapi.servicecenter.models.serialize_object.JobOrderSerialize;
import com.fusiotec.servicecenterapi.servicecenter.network.RetrofitRequestManager;
import com.fusiotec.servicecenterapi.servicecenter.utilities.Constants;
import com.fusiotec.servicecenterapi.servicecenter.utilities.Utils;
import com.github.ybq.endless.Endless;
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
    RealmResults<JobOrders> jobOrders;
    EditText et_search;
    AppCompatSpinner sp_filter;
    JobOrdersAdapter jobOrdersAdapter;
    SwipeRefreshLayout swipeContainer;

    RecyclerView recyclerView;
    Endless endless;

    final public static int SHOW_OPEN_JOB_ORDERS = 1;
    final public static int SHOW_HISTORY = 2;
    final public static int SHOW_JOB_ORDERS_BY_CUSTOMER = 3;
    final public static int SHOW_JOB_ORDERS_BY_STATION = 4;
    final public static int SHOW_JOB_ORDERS_BY_SERIAL = 5;
    final public static int SHOW_JOB_ORDERS_BY_STATUS = 6;

    final public static int REQUEST_GET_JOB_ORDERS = 302;

    final public static String SHOW = "show";
    final public static String CUSTOMER_ID = "customer_id";
    final public static String STATION_ID = "station_id";

    int show_type = SHOW_OPEN_JOB_ORDERS;
    int previous_show_type = SHOW_OPEN_JOB_ORDERS;

    Customers selected_customer;
    int station_id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_order_list);

        realm.executeTransaction(new Realm.Transaction(){
            @Override
            public void execute(Realm realm) {
                realm.delete(JobOrderDiagnosis.class);
                realm.delete(JobOrderForReturn.class);
                realm.delete(JobOrderRepairStatus.class);
                realm.delete(JobOrders.class);
                realm.delete(JobOrderShipping.class);
            }
        });

        show_type = getIntent().getExtras().getInt(SHOW, SHOW_OPEN_JOB_ORDERS);
        previous_show_type = show_type;
        if(show_type == SHOW_JOB_ORDERS_BY_CUSTOMER){
            selected_customer = realm.where(Customers.class)
                    .equalTo("id",getIntent().getExtras().getInt(CUSTOMER_ID, 0))
                    .findFirst();
        }
        station_id = accounts.getStation_id();
        if(show_type == SHOW_JOB_ORDERS_BY_STATION){
            station_id = getIntent().getExtras().getInt(STATION_ID, 0);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initUI();

        getJobOrders(et_search.getText().toString(),Constants.FIRST_LOAD);
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
        if(endless != null) endless.loadMoreComplete();
        showProgress(false);
        switch (process){
            case REQUEST_GET_JOB_ORDERS:
                setJobOrder(response);
                if(jobOrders.size() < 7){
                    endless.setLoadMoreAvailable(false);
                }
                break;
        }
    }
    public RealmQuery<JobOrders> getStartingQuery(){
        switch (show_type){
            case SHOW_JOB_ORDERS_BY_STATUS:
            case SHOW_JOB_ORDERS_BY_SERIAL:
            case SHOW_JOB_ORDERS_BY_STATION:
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
        jobOrders = getStartingQuery().findAllSorted("date_modified", Sort.DESCENDING);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh(){
                search(et_search.getText().toString(),sp_filter.getSelectedItemPosition(),Constants.SWIPE_DOWN);
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.rv_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        jobOrdersAdapter = new JobOrdersAdapter(this,jobOrders,realm.where(Stations.class).findAll());
        jobOrdersAdapter.setAccounts(accounts);
        recyclerView.setAdapter(jobOrdersAdapter);

        et_search = (EditText) findViewById(R.id.et_search);
        sp_filter = (AppCompatSpinner) findViewById(R.id.sp_filter);
        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_UNSPECIFIED){
                    search(et_search.getText().toString(),sp_filter.getSelectedItemPosition(),Constants.FIRST_LOAD);
                    return true;
                }
                return false;
            }
        });
        setLoadMoreInit();
    }
    public void setLoadMoreInit(){
        View loadingView = View.inflate(this, R.layout.layout_loading, null);
        endless = Endless.applyTo(recyclerView, loadingView);
        endless.setLoadMoreListener(new Endless.LoadMoreListener(){
            @Override
            public void onLoadMore(int page){
                search(et_search.getText().toString(),sp_filter.getSelectedItemPosition(),Constants.SWIPE_UP);
            }
        });
    }
    public void search(String search, int filter,int direction){
        switch (filter){
            case 0:
                show_type = previous_show_type;
                jobOrders = getStartingQuery()
                        .contains("id",search, Case.INSENSITIVE)
                        .findAllSorted("date_modified", Sort.DESCENDING);
                break;
            case 1:
                show_type = SHOW_JOB_ORDERS_BY_STATUS;
                JobOrderStatus jobOrderStatus = realm.where(JobOrderStatus.class)
                        .equalTo("name",search, Case.INSENSITIVE).findFirst();

                if(jobOrderStatus == null){
                    Toast.makeText(this, "Not Existing Status Name", Toast.LENGTH_SHORT).show();
                    return;
                }
                search = jobOrderStatus.getId()+"";
                jobOrders = getStartingQuery()
                        .equalTo("status_id",jobOrderStatus.getId())
                        .findAllSorted("date_modified", Sort.DESCENDING);
                break;
            case 2:
                show_type = SHOW_JOB_ORDERS_BY_SERIAL;
                jobOrders = getStartingQuery()
                        .equalTo("serial_number",search, Case.INSENSITIVE)
                        .findAllSorted("date_modified", Sort.DESCENDING);
                break;
        }
        jobOrdersAdapter.setData(jobOrders);
        jobOrdersAdapter.notifyDataSetChanged();
        getJobOrders(search,direction);
    }

    @Override
    public void onBackPressed(){
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }


    public void getJobOrders(String s,int direction){
        switch (show_type){
            case SHOW_JOB_ORDERS_BY_STATION:
            case SHOW_OPEN_JOB_ORDERS:
            case SHOW_JOB_ORDERS_BY_SERIAL:
            case SHOW_JOB_ORDERS_BY_STATUS:
            case SHOW_HISTORY:
                requestManager.setRequestAsync(requestManager.getApiService().get_job_orders(show_type,0,getStationId(),s , getEndOrStartTime(direction == Constants.SWIPE_DOWN)  , jobOrders.isEmpty() ? Constants.FIRST_LOAD : direction),REQUEST_GET_JOB_ORDERS);
                break;
            case SHOW_JOB_ORDERS_BY_CUSTOMER:
                requestManager.setRequestAsync(requestManager.getApiService().get_job_orders(show_type,selected_customer.getId(),getStationId(),s, getEndOrStartTime(direction == Constants.SWIPE_DOWN)  , jobOrders.isEmpty() ? Constants.FIRST_LOAD : direction),REQUEST_GET_JOB_ORDERS);
                break;
        }
    }
    public int getStationId(){
        return (show_type == SHOW_JOB_ORDERS_BY_STATION) ? station_id : (accounts.isAdmin() || accounts.isMainBranch() ? 0  : station_id);
    }
    public String getEndOrStartTime(boolean swipe_down){
        if(!jobOrders.isEmpty()){
            return Utils.dateToString(jobOrders.where().findAllSorted("date_modified",swipe_down ? Sort.DESCENDING : Sort.ASCENDING).get(0).getDate_modified(),"yyyy-MM-dd HH:mm:ss");
        }
        return "";
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
                Toast.makeText(this, "No More Results", Toast.LENGTH_SHORT).show();
                return false;
            }
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
