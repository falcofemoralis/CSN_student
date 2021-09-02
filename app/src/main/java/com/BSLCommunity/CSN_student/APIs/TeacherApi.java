package com.BSLCommunity.CSN_student.APIs;

import com.BSLCommunity.CSN_student.Models.TeacherModel;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;

public interface TeacherApi {
    String RESERVE_URL = "https://peaceful-springs-87108.herokuapp.com/";
    String BASE_URL = "http://f0513611.xsph.ru/";
    String TEACHER_API = "api/teachers";

    @GET(TEACHER_API + "/all")
    Call<ArrayList<TeacherModel.Teacher>> allTeachers();
}
