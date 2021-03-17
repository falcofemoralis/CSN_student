package com.BSLCommunity.CSN_student.APIs;

import com.BSLCommunity.CSN_student.Models.Subject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface SubjectApi {
    String BASE_URL = "http://192.168.0.104:81/";
    String SUBJECT_API = "api/subjects";

    @GET(SUBJECT_API + "/group/{id}")
    Call<ArrayList<Subject>> groupSubjects(@Path("id") int idGroup);
}
