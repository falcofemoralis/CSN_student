package com.BSLCommunity.CSN_student.APIs;

import com.BSLCommunity.CSN_student.Models.AchievementsModel;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;

public interface AchievementsApi {
    String RESERVE_URL = "https://peaceful-springs-87108.herokuapp.com/";
    String BASE_URL = "http://f0513611.xsph.ru/";
    String LOCAL_URL = "http://192.168.0.100:81/";
    String ACHIEVEMENTS_API = "api/achievements";

    @GET(ACHIEVEMENTS_API + "/all")
    Call<ArrayList<AchievementsModel.Achievement>> getAchievements();
}
