package com.fusiotec.servicecenterapi.servicecenter.utilities;

import android.util.Log;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.RealmModel;
import io.realm.RealmResults;

/**
 * Created by Owner on 7/19/2017.
 */

public class RealmModelGenericHelper<T extends RealmModel> {
    public void initClassForChanges(final Class clazz,RealmResults<T> realmResults, final RealmModelChangeListener listener){
        Log.e(""+clazz.getSimpleName(),""+realmResults.size());
        realmResults.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<T>>() {
            @Override
            public void onChange(RealmResults<T> list, OrderedCollectionChangeSet changeSet){
                listener.showUnsyncRows(list.size());
            }
        });
    }
    public interface RealmModelChangeListener{
        void showUnsyncRows(int size);
    }
}
