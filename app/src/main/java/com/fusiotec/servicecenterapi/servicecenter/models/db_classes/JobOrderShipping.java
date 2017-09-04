package com.fusiotec.servicecenterapi.servicecenter.models.db_classes;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Owner on 8/8/2017.
 */

public class JobOrderShipping extends RealmObject {
    public final static String TABLE_NAME = "job_order_shipping";

    @PrimaryKey
    private String job_order_id;
    private String shipping_no;
    private String shipping_note;
    private int account_id;
    private Date date_created;
    private Date date_modified;
    private int is_saved_online;

    public String getJob_order_id() {
        return job_order_id;
    }

    public void setJob_order_id(String job_order_id) {
        this.job_order_id = job_order_id;
    }

    public int getAccount_id() {
        return account_id;
    }

    public void setAccount_id(int account_id) {
        this.account_id = account_id;
    }

    public Date getDate_created() {
        return date_created;
    }

    public void setDate_created(Date date_created) {
        this.date_created = date_created;
    }

    public Date getDate_modified() {
        return date_modified;
    }

    public void setDate_modified(Date date_modified) {
        this.date_modified = date_modified;
    }

    public String getShipping_no() {
        return shipping_no;
    }

    public void setShipping_no(String shipping_no) {
        this.shipping_no = shipping_no;
    }

    public int getIs_saved_online() {
        return is_saved_online;
    }

    public void setIs_saved_online(int is_saved_online) {
        this.is_saved_online = is_saved_online;
    }

    public String getShipping_note() {
        return shipping_note;
    }

    public void setShipping_note(String shipping_note) {
        this.shipping_note = shipping_note;
    }
}
