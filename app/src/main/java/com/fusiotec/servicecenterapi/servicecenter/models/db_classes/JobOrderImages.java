package com.fusiotec.servicecenterapi.servicecenter.models.db_classes;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Owner on 8/8/2017.
 */

public class JobOrderImages extends RealmObject{
    public final static String TABLE_NAME = "job_order_images";

    @PrimaryKey
    private int id;
    private String image;
    private String label = "";
    private String job_order_id;
    private int job_order_status_id;
    private Date date_created;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getJob_order_id() {
        return job_order_id;
    }

    public void setJob_order_id(String job_order_id) {
        this.job_order_id = job_order_id;
    }

    public int getJob_order_status_id() {
        return job_order_status_id;
    }

    public void setJob_order_status_id(int job_order_status_id) {
        this.job_order_status_id = job_order_status_id;
    }

    public Date getDate_created() {
        return date_created;
    }

    public void setDate_created(Date date_created) {
        this.date_created = date_created;
    }
}
