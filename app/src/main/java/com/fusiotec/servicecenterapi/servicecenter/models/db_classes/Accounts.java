package com.fusiotec.servicecenterapi.servicecenter.models.db_classes;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Owner on 8/6/2017.
 */

public class Accounts extends RealmObject {

    public final static String TABLE_NAME = "accounts";

    final public static int SERVICE_CENTER = 1;
    final public static int MAIN_BRANCH = 2;

    @PrimaryKey
    private int id;
    private String first_name = "";
    private String last_name = "";
    private String username = "";
    private String password = "";
    private String email = "";
    private String phone_no = "";
    private String image = "";
    private int account_type_id;
    private Integer approved_by;
    private Date date_approved;
    private Date date_created;
    private Date date_modified;
    private int is_main_branch;
    private int station_id;
    private int is_deleted;

    private Stations station;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public int getAccount_type_id() {
        return account_type_id;
    }

    public void setAccount_type_id(int account_type_id) {
        this.account_type_id = account_type_id;
    }

    public int getApproved_by() {
        return approved_by;
    }

    public void setApproved_by(int approved_by) {
        this.approved_by = approved_by;
    }

    public Date getDate_approved() {
        return date_approved;
    }

    public void setDate_approved(Date date_approved) {
        this.date_approved = date_approved;
    }

    public Stations getStation() {
        return station;
    }

    public void setStation(Stations station) {
        this.station = station;
    }

    public int getIs_main_branch() {
        return is_main_branch;
    }

    public boolean isAdmin(){
        return is_main_branch == 2;
    }
    public boolean isMainBranch(){
        return account_type_id == MAIN_BRANCH;
    }

    public void setIs_main_branch(int is_main_branch) {
        this.is_main_branch = is_main_branch;
    }

    public int getStation_id() {
        return station_id;
    }

    public void setStation_id(int station_id) {
        this.station_id = station_id;
    }

    public int getIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(int is_deleted) {
        this.is_deleted = is_deleted;
    }
}
