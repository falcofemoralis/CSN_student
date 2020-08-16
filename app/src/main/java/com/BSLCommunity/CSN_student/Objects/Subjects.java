package com.BSLCommunity.CSN_student.Objects;

import android.content.Context;
import android.widget.Toast;

import com.BSLCommunity.CSN_student.Activities.Main;
import com.BSLCommunity.CSN_student.Managers.JSONHelper;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.concurrent.Callable;

public class Subjects {
    public class SubjectsList {
        public int Code_Lector, Code_Practice, Code_Assistant;
        public String NameDiscipline;
    }
    public static SubjectsList[] subjectsList;

    public static void getSubjectsList(final Context context, final Callable<Void> methodParam) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = Main.MAIN_URL + String.format("api/subjects/?Code_Group=%1$s", User.getInstance().groupId);

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                subjectsList = gson.fromJson(response, SubjectsList[].class);
                try {
                    methodParam.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "No connection with our server,try later...", Toast.LENGTH_SHORT).show();

                try {
                    String response = JSONHelper.read(context, Main.GROUP_FILE_NAME);
                    Gson gson = new Gson();
                    subjectsList = gson.fromJson(response, SubjectsList[].class);
                    try {
                        methodParam.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {

                }
            }
        });
        requestQueue.add(request);
    }
}
