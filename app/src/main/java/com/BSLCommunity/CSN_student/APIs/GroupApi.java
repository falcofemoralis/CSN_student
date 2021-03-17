package com.BSLCommunity.CSN_student.APIs;

import com.BSLCommunity.CSN_student.Models.GroupModel;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GroupApi {
    String BASE_URL = "http://f0513611.xsph.ru/";
    String GROUP_API = "api/groups";

    @GET(GROUP_API + "/all")
    Call<ArrayList<GroupModel.Group>> allGroups();

    @GET(GROUP_API + "/names")
    Call<ArrayList<GroupModel.Group>> getGroupNames();
}
