package com.fusiotec.servicecenterapi.servicecenter.models.db_classes;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Owner on 8/19/2017.
 */

public class JobOrderStatus extends RealmObject {
    @PrimaryKey
    private int id;
    private String name;

    public JobOrderStatus(){

    }

    public JobOrderStatus(int id,String name){
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
