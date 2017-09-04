package com.fusiotec.servicecenterapi.servicecenter.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.fusiotec.servicecenterapi.servicecenter.manager.LocalStorage;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.Accounts;

import io.realm.Realm;


/**
 * Created by Owner on 5/19/2017.
 */

public abstract class BaseFragment extends Fragment{
    Realm realm;
    LocalStorage ls;
    Accounts accounts;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
        accounts = realm.where(Accounts.class).findFirst();
        ls = new LocalStorage(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}