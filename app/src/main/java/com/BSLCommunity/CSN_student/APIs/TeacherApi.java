package com.BSLCommunity.CSN_student.APIs;

import com.BSLCommunity.CSN_student.Models.ScheduleList;
import com.BSLCommunity.CSN_student.Models.TeacherModel;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface TeacherApi {
    String BASE_URL = "http://192.168.0.104:81/";
    String TEACHER_API = "api/teachers";

    @GET(TEACHER_API + "/all")
    Call<ArrayList<TeacherModel.Teacher>> allTeachers();

    @GET(TEACHER_API + "/{teacherId}/schedule")
    Call<ArrayList<ScheduleList>> scheduleByTeacherId(@Path("teacherId") int teacherId);
}
