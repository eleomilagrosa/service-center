package com.fusiotec.servicecenterapi.servicecenter.models.db_classes;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Owner on 9/24/2017.
 */

public class Printers extends RealmObject{

    public Printers(){
    }
    public Printers(String name){
        this.name = name;
    }

    @PrimaryKey
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
