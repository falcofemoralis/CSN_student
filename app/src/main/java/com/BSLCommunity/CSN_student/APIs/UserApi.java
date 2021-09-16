package com.BSLCommunity.CSN_student.APIs;

import com.BSLCommunity.CSN_student.Models.AchievementsModel;
import com.BSLCommunity.CSN_student.Models.EditableSubject;
import com.BSLCommunity.CSN_student.Models.User;
import com.BSLCommunity.CSN_student.Models.UserLog;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface UserApi {
    String RESERVE_URL = "https://csn-student.herokuapp.com/api/users/";
    String API_URL = "http://f0513611.xsph.ru/api/users/";
    String LOCAL_URL = "http://192.168.0.100:81/api/users/";

    @GET("login")
    Call<User> login(@Query("nickname") String nickname, @Query("password") String password);

    @FormUrlEncoded
    @POST(".")
    Call<User> registration(@Field("nickname") String nickname, @Field("password") String password, @Field("group") String groupName);

    @PUT(".")
    Call<Void> updateUserData(@Header("token") String token, @Body JsonObject data);

    @GET("rating")
    Call<ArrayList<EditableSubject>> getUserRating(@Header("token") String token);

    @PUT("rating")
    Call<Void> updateUserRating(@Header("token") String token, @Body ArrayList<EditableSubject> data);

    @PUT("opens")
    Call<Void> updateUserOpens(@Header("token") String token, @Body int visits);

    @PUT("activity")
    Call<Void> updateUserActivity(@Header("token") String token, @Body ArrayList<UserLog> logs);

    @PUT("achievements")
    Call<Void> updateUserAchievements(@Header("token") String token, @Body ArrayList<AchievementsModel.UserAchievement> data);

    @GET("achievements")
    Call<ArrayList<AchievementsModel.UserAchievement>> getUserAchievements(@Header("token") String token);
}
