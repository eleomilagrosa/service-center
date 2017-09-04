package com.fusiotec.servicecenterapi.servicecenter.network;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Owner on 3/22/2017.
 */

public interface ApiService{
    //get original date
    @GET("date")
    Call<GenericReceiver> kroid_get_original_date();

    @FormUrlEncoded
    @POST("oauth/token")
    Call<Token> kroid_get_token(@Field("grant_type") String grant_type, @Field("client_id") String client_id, @Field("client_secret") String client_secret, @Field("username") String username, @Field("password") String password, @Field("scope") String scope);

    @GET("/api/accountVerification/{username}/{password}")
    Call<GenericReceiver> kroid_check_if_account_exist(@Path("username") String username, @Path("password") String password);

}
