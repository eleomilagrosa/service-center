package com.fusiotec.servicecenterapi.servicecenter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.fusiotec.servicecenterapi.servicecenter.R;
import com.fusiotec.servicecenterapi.servicecenter.adapters.AccountListAdapter;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.Accounts;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.Stations;

import io.realm.Case;
import io.realm.RealmResults;

/**
 * Created by Owner on 9/3/2017.
 */

public class AdminListActivity extends BaseActivity {

    AccountListAdapter accountListAdapter;
    RealmResults<Accounts> account_list;
    EditText et_search;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initList();
        initSearch();
    }
    public void initList(){
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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
}