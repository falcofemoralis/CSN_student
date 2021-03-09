package com.BSLCommunity.CSN_student.APIs;

import com.BSLCommunity.CSN_student.Managers.DBHelper;
import com.BSLCommunity.CSN_student.Models.GroupModel;
import com.BSLCommunity.CSN_student.Models.ScheduleList;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

<<<<<<< HEAD:app/src/main/java/com/BSLCommunity/CSN_student/Managers/Internet/RequestApi.java
public interface RequestApi {
    String BASE_URL = DBHelper.MAIN_URL;
    String TEACHER_API = "api/teachers";
    String USER_API = "api/users";
=======
public interface GroupApi {
    String BASE_URL = "http://192.168.1.3/";
>>>>>>> remotes/origin/rebuild-code:app/src/main/java/com/BSLCommunity/CSN_student/APIs/GroupApi.java
    String GROUP_API = "api/groups";

    @GET(GROUP_API + "/all")
    Call<ArrayList<GroupModel.Group>> allGroups();

    @GET(GROUP_API + "/{groupId}/schedule")
    Call<ArrayList<ScheduleList>>  scheduleByGroupId(@Path("groupId") int groupId);
}
