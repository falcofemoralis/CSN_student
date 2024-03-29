package com.BSLCommunity.CSN_student.APIs;

import com.BSLCommunity.CSN_student.Models.DataModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CacheApi {
    String RESERVE_URL = "https://csn-student.herokuapp.com/";
    String BASE_URL = "http://f0513611.xsph.ru/";
    String LOCAL_URL = "http://192.168.0.100:81/";
    String CACHE_API = "api/cache";

    @GET(CACHE_API + "/check")
    Call<String> checkCache(@Query("creationTime") int time);

    @GET(CACHE_API + "/download")
    Call<DataModel.Cache> downloadCache();
}
