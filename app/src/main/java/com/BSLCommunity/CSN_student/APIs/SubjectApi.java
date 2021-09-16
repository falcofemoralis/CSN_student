package com.BSLCommunity.CSN_student.APIs;

import com.BSLCommunity.CSN_student.Models.Subject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface SubjectApi {
    String RESERVE_URL = "https://csn-student.herokuapp.com/";
    String BASE_URL = "http://f0513611.xsph.ru/";
    String SUBJECT_API = "api/subjects";

    @GET(SUBJECT_API + "/group/{id}")
    Call<ArrayList<Subject>> groupSubjects(@Path("id") int idGroup);
}
