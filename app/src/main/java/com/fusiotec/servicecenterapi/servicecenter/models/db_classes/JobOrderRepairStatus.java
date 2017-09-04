package com.fusiotec.servicecenterapi.servicecenter.models.db_classes;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Owner on 8/8/2017.
 */

public class JobOrderRepairStatus extends RealmObject {
    public final static String TABLE_NAME = "job_order_repair_status";

    @PrimaryKey
    private String job_order_id;
    private String repair_note;
    private int repair_status;
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

    public String getRepair_note() {
        return repair_note;
    }

    public void setRepair_note(String repair_note) {
        this.repair_note = repair_note;
    }

    public int getRepair_status() {
        return repair_status;
    }

    public void setRepair_status(int repair_status) {
        this.repair_status = repair_status;
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

    public int getIs_saved_online() {
        return is_saved_online;
    }

    public void setIs_saved_online(int is_saved_online) {
        this.is_saved_online = is_saved_online;
    }
}
