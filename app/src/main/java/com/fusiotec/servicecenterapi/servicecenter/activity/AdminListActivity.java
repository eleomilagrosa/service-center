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
import com.fusiotec.servicecenterapi.servicecenter.adapters.AccountListAdapter;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.Accounts;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.Stations;
import com.fusiotec.servicecenterapi.servicecenter.models.serialize_object.AccountsSerialize;
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

public class AdminListActivity extends BaseActivity {

    AccountListAdapter accountListAdapter;
    RealmResults<Accounts> account_list;
    EditText et_search;
    SwipeRefreshLayout swipeContainer;

    public final static int REQUEST_GET_ACCOUNTS = 301;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initList();
        initSearch();
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
    public void setReceiver(String response,int process,int status) {
        switch (process){
            case REQUEST_GET_ACCOUNTS:
                setAccount(response);
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
                getAccounts(et_search.getText().toString());
            }
        });

        account_list = realm.where(Accounts.class).equalTo("is_deleted",0).notEqualTo("id",accounts.getId()).equalTo("is_main_branch",1).findAll();
        accountListAdapter = new AccountListAdapter(this,account_list,realm.where(Stations.class).findAll());
        recyclerView.setAdapter(accountListAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
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
        showProgress(true);
        account_list = realm.where(Accounts.class)
                .equalTo("is_deleted",0)
                .notEqualTo("id",accounts.getId())
                .equalTo("is_main_branch",1)
                    .beginGroup()
                    .contains("last_name",s, Case.INSENSITIVE)
                    .or()
                    .contains("first_name",s, Case.INSENSITIVE)
                    .endGroup()
                .isNull("date_approved").findAll();
        setList();
        getAccounts(s);
    }

    public void setList(){
        accountListAdapter.setData(account_list);
        accountListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed(){
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public void getAccounts(String s){
        requestManager.setRequestAsync(requestManager.getApiService().get_accounts(3,0,s),REQUEST_GET_ACCOUNTS);
    }
    public boolean setAccount(String response){
        try{
            JSONObject jsonObject = new JSONObject(response);
//            if(jsonObject.getInt(RetrofitRequestManager.SUCCESS) == 1){
            JSONArray jsonArray = jsonObject.getJSONArray(Accounts.TABLE_NAME);
            final ArrayList<Accounts> accounts = new GsonBuilder()
                    .registerTypeAdapter(Accounts.class,new AccountsSerialize())
                    .setDateFormat("yyyy-MM-dd HH:mm:ss").create()
                    .fromJson(jsonArray.toString(), new TypeToken<List<Accounts>>(){}.getType());
            if(!accounts.isEmpty()){
                realm.executeTransaction(new Realm.Transaction(){
                    @Override
                    public void execute(Realm realm){
                    for(Accounts account:accounts){
                        Stations station = realm.where(Stations.class).equalTo("id",account.getStation_id()).findFirst();
                        if(station != null){
                            account.setStation(station);
                            realm.copyToRealmOrUpdate(account);
                        }
                    }
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
