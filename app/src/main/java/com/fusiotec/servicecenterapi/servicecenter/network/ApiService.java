package com.fusiotec.servicecenterapi.servicecenter.network;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Owner on 3/22/2017.
 */

public interface ApiService{
    //get original date
    @GET("date.php")
    Call<GenericReceiver> get_original_date();

    @FormUrlEncoded
    @POST("create_account.php")
    Call<GenericReceiver> create_account(@Field("first_name") String first_name,
                                         @Field("last_name") String last_name,
                                         @Field("username") String username,
                                         @Field("password") String password,
                                         @Field("email") String email,
                                         @Field("phone_no") String phone_no,
                                         @Field("account_type_id") int account_type_id,
                                         @Field("station_id") int station_id);
    @FormUrlEncoded
    @POST("update_account.php")
    Call<GenericReceiver> update_account(@Field("id") int id,
                                               @Field("first_name") String first_name,
                                               @Field("last_name") String last_name,
                                               @Field("email") String email,
                                               @Field("phone_no") String phone_no,
                                               @Field("account_type_id") int account_type_id,
                                               @Field("station_id") int station_id);
    @FormUrlEncoded
    @POST("approved_account.php")
    Call<GenericReceiver> approved_account(@Field("id") int id,
                                               @Field("approved_by") int approved_by);
    @FormUrlEncoded
    @POST("update_account_status_id.php")
    Call<GenericReceiver> update_account_status_id(@Field("id") int id,
                                               @Field("is_main_branch") int is_main_branch);

    @FormUrlEncoded
    @POST("delete_account.php")
    Call<GenericReceiver> delete_account(@Field("id") int id);

    @FormUrlEncoded
    @POST("change_password.php")
    Call<GenericReceiver> change_password(@Field("id") int id,@Field("password") String password);

    @GET("get_accounts.php")
    Call<GenericReceiver> get_accounts(@Query("get_list_type") int get_list_type,@Query("station_id") int station_id,@Query("name") String name);

    @GET("get_stations.php")
    Call<GenericReceiver> get_stations(@Query("name") String name);

    @FormUrlEncoded
    @POST("create_station.php")
    Call<GenericReceiver> create_station(@Field("station_name") String station_name,
                                         @Field("station_prefix") String station_prefix,
                                         @Field("station_address") String station_address,
                                         @Field("station_number") String station_number,
                                         @Field("station_description") String station_description);
    @FormUrlEncoded
    @POST("update_station.php")
    Call<GenericReceiver> update_station(@Field("id") int id,
                                         @Field("station_name") String station_name,
                                         @Field("station_prefix") String station_prefix,
                                         @Field("station_address") String station_address,
                                         @Field("station_number") String station_number,
                                         @Field("station_description") String station_description);

    @FormUrlEncoded
    @POST("delete_stations.php")
    Call<GenericReceiver> delete_stations(@Field("id") int id);

    @GET("get_customers.php")
    Call<GenericReceiver> get_customers(@Query("station_id") int station_id);

    @FormUrlEncoded
    @POST("create_customers.php")
    Call<GenericReceiver> create_customers(@Field("first_name") String first_name,
                                         @Field("last_name") String last_name,
                                         @Field("address") String address,
                                         @Field("phone_no") String phone_no,
                                         @Field("email") String email,
                                         @Field("account_id") int account_id,
                                         @Field("station_id") int station_id);
    @FormUrlEncoded
    @POST("update_customers.php")
    Call<GenericReceiver> update_customers(@Field("id") int id,
                                         @Field("first_name") String first_name,
                                         @Field("last_name") String last_name,
                                         @Field("address") String address,
                                         @Field("phone_no") String phone_no,
                                         @Field("email") String email,
                                         @Field("account_id") int account_id);
    @FormUrlEncoded
    @POST("delete_customer.php")
    Call<GenericReceiver> delete_customer(@Field("id") int id);

    @GET("get_job_orders.php")
    Call<GenericReceiver> get_job_orders(@Query("get_list_type") int get_list_type,@Query("customer_id") int customer_id,@Query("station_id") int station_id,@Query("id") String id);

    @GET("get_job_order_by_id.php")
    Call<GenericReceiver> get_job_order_by_id(@Query("id") String id);

    @FormUrlEncoded
    @POST("login.php")
    Call<GenericReceiver> login(@Field("username") String username, @Field("password") String password);



    @FormUrlEncoded
    @POST("create_job_order.php")
    Call<GenericReceiver> create_job_order(@Field("id") String id,
                                               @Field("unit") String unit,
                                               @Field("model") String model,
                                               @Field("date_of_purchased") String date_of_purchased,
                                               @Field("dealer") String dealer,
                                               @Field("serial_number") String serial_number,
                                               @Field("warranty_label") String warranty_label,
                                               @Field("complaint") String complaint,
                                               @Field("customer_id") int customer_id,
                                               @Field("account_id") int account_id,
                                               @Field("status_id") int status_id,
                                               @Field("station_id") int station_id);

    @FormUrlEncoded
    @POST("create_job_order_diagnosis.php")
    Call<GenericReceiver> create_job_order_diagnosis(@Field("job_order_id") String job_order_id,
                                                       @Field("diagnosis") String diagnosis,
                                                       @Field("account_id") int account_id);

    @FormUrlEncoded
    @POST("create_job_order_repair_status.php")
    Call<GenericReceiver> create_job_order_repair_status(@Field("job_order_id") String job_order_id,
                                                       @Field("repair_note") String repair_note,
                                                       @Field("repair_status") int repair_status,
                                                       @Field("account_id") int account_id);

    @FormUrlEncoded
    @POST("create_job_order_for_return.php")
    Call<GenericReceiver> create_job_order_for_return(@Field("job_order_id") String job_order_id,
                                                       @Field("shipping_no") String shipping_no,
                                                       @Field("shipping_note") String shipping_note,
                                                       @Field("account_id") int account_id,
                                                       @Field("repair_status") int repair_status);

    @FormUrlEncoded
    @POST("create_job_order_shipping.php")
    Call<GenericReceiver> create_job_order_shipping(@Field("job_order_id") String job_order_id,
                                                       @Field("shipping_no") String shipping_no,
                                                       @Field("shipping_note") String shipping_note,
                                                       @Field("account_id") int account_id);

    @FormUrlEncoded
    @POST("update_job_order_receive_status.php")
    Call<GenericReceiver> update_job_order_receive_status(@Field("job_order_id") String job_order_id,
                                                       @Field("status_id") int status_id,
                                                       @Field("repair_status") int repair_status);

    @Multipart
    @POST("upload_job_order_image.php")
    Call<GenericReceiver> upload_job_order_image(@Part MultipartBody.Part uploaded_file,
                                      @Part("label") RequestBody label,
                                      @Part("job_order_status_id") RequestBody job_order_status_id,
                                      @Part("job_order_id")RequestBody job_order_id);
}
