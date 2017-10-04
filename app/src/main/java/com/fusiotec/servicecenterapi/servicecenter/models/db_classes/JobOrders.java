package com.fusiotec.servicecenterapi.servicecenter.models.db_classes;

import java.util.ArrayList;
import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Owner on 8/8/2017.
 */

public class JobOrders extends RealmObject {

    public final static String TABLE_NAME = "job_orders";

    public final static String JOB_ORDER_ID = "job_order_id";

    public final static int ACTION_PROCESSING = 1;
    public final static int ACTION_DIAGNOSED = 2;
    public final static int ACTION_FORWARDED = 3;
    public final static int ACTION_RECEIVE_AT_MAIN = 4;
    public final static int ACTION_FOR_RETURN = 5;
    public final static int ACTION_RECEIVE_AT_SC = 6;
    public final static int ACTION_PICK_UP = 7;
    public final static int ACTION_CLOSED = 8;


    @PrimaryKey
    private String id;
    private String unit;
    private String model;
    private String dealer;
    private Date date_of_purchased;
    private String serial_number;
    private String warranty_label;
    private String complaint;
    private int customer_id;
    private int station_id;
    private int account_id;
    private int status_id;
    private int repair_status;
    private Date date_created;
    private Date date_modified;
    private Date date_time_closed;

    private JobOrderDiagnosis jobOrderDiagnosis;
    private JobOrderShipping jobOrderShipping;
    private JobOrderRepairStatus jobOrderRepairStatus;
    private JobOrderForReturn jobOrderForReturn;
    private Customers customer;

    private RealmList<JobOrderImages> jobOrderImages = new RealmList<>();

    @Ignore
    private ArrayList<JobOrderImages> jobOrderImageslist = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Date getDate_of_purchased() {
        return date_of_purchased;
    }

    public void setDate_of_purchased(Date date_of_purchased) {
        this.date_of_purchased = date_of_purchased;
    }

    public String getSerial_number() {
        return serial_number;
    }

    public void setSerial_number(String serial_number) {
        this.serial_number = serial_number;
    }

    public String getWarranty_label() {
        return warranty_label;
    }

    public void setWarranty_label(String warranty_label) {
        this.warranty_label = warranty_label;
    }

    public String getComplaint() {
        return complaint;
    }

    public void setComplaint(String complaint) {
        this.complaint = complaint;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
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

    public JobOrderDiagnosis getJobOrderDiagnosis() {
        return jobOrderDiagnosis;
    }

    public void setJobOrderDiagnosis(JobOrderDiagnosis jobOrderDiagnosis) {
        this.jobOrderDiagnosis = jobOrderDiagnosis;
    }

    public JobOrderShipping getJobOrderShipping() {
        return jobOrderShipping;
    }

    public void setJobOrderShipping(JobOrderShipping jobOrderShipping) {
        this.jobOrderShipping = jobOrderShipping;
    }

    public RealmList<JobOrderImages> getJobOrderImages() {
        return jobOrderImages;
    }

    public void setJobOrderImages(RealmList<JobOrderImages> jobOrderImages) {
        this.jobOrderImages = jobOrderImages;
    }

    public Customers getCustomer() {
        return customer;
    }

    public void setCustomer(Customers customer) {
        this.customer = customer;
    }

    public String getDealer() {
        return dealer;
    }

    public void setDealer(String dealer) {
        this.dealer = dealer;
    }

    public ArrayList<JobOrderImages> getJobOrderImageslist() {
        return jobOrderImageslist;
    }

    public void setJobOrderImageslist(ArrayList<JobOrderImages> jobOrderImageslist) {
        this.jobOrderImageslist = jobOrderImageslist;
    }

    public int getStatus_id() {
        return status_id;
    }

    public void setStatus_id(int status_id) {
        this.status_id = status_id;
    }

    public int getRepair_status() {
        return repair_status;
    }

    public void setRepair_status(int repair_status) {
        this.repair_status = repair_status;
    }

    public JobOrderRepairStatus getJobOrderRepairStatus() {
        return jobOrderRepairStatus;
    }

    public void setJobOrderRepairStatus(JobOrderRepairStatus jobOrderRepairStatus) {
        this.jobOrderRepairStatus = jobOrderRepairStatus;
    }

    public JobOrderForReturn getJobOrderForReturn() {
        return jobOrderForReturn;
    }

    public void setJobOrderForReturn(JobOrderForReturn jobOrderForReturn) {
        this.jobOrderForReturn = jobOrderForReturn;
    }

    public int getStation_id() {
        return station_id;
    }

    public void setStation_id(int station_id) {
        this.station_id = station_id;
    }

    public Date getDate_time_closed() {
        return date_time_closed;
    }

    public void setDate_time_closed(Date date_time_closed) {
        this.date_time_closed = date_time_closed;
    }
}
