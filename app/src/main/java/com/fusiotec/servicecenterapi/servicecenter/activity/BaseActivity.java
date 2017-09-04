package com.fusiotec.servicecenterapi.servicecenter.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.fusiotec.servicecenterapi.servicecenter.manager.LocalStorage;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.Accounts;

import io.realm.Realm;

/**
 * Created by Owner on 8/5/2017.
 */

public class BaseActivity extends AppCompatActivity{

    Realm realm;
    LocalStorage ls;
    Accounts accounts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
        ls = new LocalStorage(this);
        accounts = realm.where(Accounts.class).findFirst();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        realm.close();
    }
}
