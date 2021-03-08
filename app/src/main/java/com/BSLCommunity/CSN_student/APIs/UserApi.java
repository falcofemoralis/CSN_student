package com.BSLCommunity.CSN_student.APIs;

import com.BSLCommunity.CSN_student.Models.UserData;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UserApi {
    String API_URL = "http://192.168.1.3/api/users/";

    @FormUrlEncoded
    @POST(".")
    Call<UserData> registration(@Field("nickname") String nickname, @Field("password") String password, @Field("group") String groupName);

    @GET("login")
    Call<UserData> login(@Query("nickname") String nickname, @Query("password") String password);
}
