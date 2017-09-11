package com.fusiotec.servicecenterapi.servicecenter.network;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Owner on 3/22/2017.
 */

public interface ApiService{
    //get original date
    @GET("date.php")
    Call<GenericReceiver> get_original_date();

    @POST("login.php")
    Call<GenericReceiver> login(@Field("username") String username, @Field("password") String password);

    @POST("create_customer_info.php")
    Call<GenericReceiver> create_customer_info(@Field("first_name") String first_name,
                                               @Field("last_name") String last_name,
                                               @Field("address") String address,
                                               @Field("phone_no") String phone_no,
                                               @Field("email") String email,
                                               @Field("station_id") String station_id);
    @POST("create_job_order.php")
    Call<GenericReceiver> create_job_order(@Field("id") String id,
                                               @Field("unit") String unit,
                                               @Field("model") String model,
                                               @Field("date_of_purchased") String date_of_purchased,
                                               @Field("dealer") String dealer,
                                               @Field("serial_number") String serial_number,
                                               @Field("warranty_label") String warranty_label,
                                               @Field("complaint") String complaint,
                                               @Field("customer_id") String customer_id,
                                               @Field("account_id") String account_id,
                                               @Field("status_id") String status_id,
                                               @Field("station_id") String station_id);

    @POST("create_job_order_diagnosis.php")
    Call<GenericReceiver> create_job_order_diagnosis(@Field("job_order_id") String job_order_id,
                                                       @Field("diagnosis") String diagnosis,
                                                       @Field("account_id") String account_id);
    @POST("create_job_order_repair_status.php")
    Call<GenericReceiver> create_job_order_repair_status(@Field("job_order_id") String job_order_id,
                                                       @Field("repair_note") String repair_note,
                                                       @Field("repair_status") String repair_status,
                                                       @Field("account_id") String account_id);
    @POST("create_job_order_for_return.php")
    Call<GenericReceiver> create_job_order_for_return(@Field("job_order_id") String job_order_id,
                                                       @Field("shipping_no") String shipping_no,
                                                       @Field("shipping_note") String shipping_note,
                                                       @Field("account_id") String account_id,
                                                       @Field("repair_status") String repair_status);
    @POST("create_job_order_shipping.php")
    Call<GenericReceiver> create_job_order_shipping(@Field("job_order_id") String job_order_id,
                                                       @Field("shipping_no") String shipping_no,
                                                       @Field("shipping_note") String shipping_note,
                                                       @Field("account_id") String account_id);
    @POST("create_job_order_shipping.php")
    Call<GenericReceiver> update_job_order_receive_status(@Field("job_order_id") String job_order_id,
                                                       @Field("status_id") String status_id,
                                                       @Field("repair_status") String repair_status);
}
