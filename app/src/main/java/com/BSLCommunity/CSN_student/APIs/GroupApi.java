package com.BSLCommunity.CSN_student.APIs;

import com.BSLCommunity.CSN_student.Models.GroupModel;
import com.BSLCommunity.CSN_student.Models.ScheduleList;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GroupApi {
    String BASE_URL = "http://192.168.0.100:81/";
    String GROUP_API = "api/groups";

    @GET(GROUP_API + "/all")
    Call<ArrayList<GroupModel.Group>> allGroups();

    @GET(GROUP_API + "/names")
    Call<ArrayList<GroupModel.Group>> getGroupNames();
}
