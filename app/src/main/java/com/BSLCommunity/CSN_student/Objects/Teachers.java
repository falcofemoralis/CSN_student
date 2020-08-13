package com.BSLCommunity.CSN_student.Objects;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.BSLCommunity.CSN_student.Activities.Main;
import com.BSLCommunity.CSN_student.Managers.JSONHelper;
import com.BSLCommunity.CSN_student.R;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;

public class Teachers {
    public class TeachersList { public int teacher_id; public String FIO;}

    public static Map<Integer, String> teachersList = new HashMap<>();

    /* Функция получение учителей в университете
     * Параметры:
     * appContext - application context
     * course - номер курса
     * */
    public static void getTeachers(final Context appContext, final Callable<Void> methodParam) {
        RequestQueue requestQueue = Volley.newRequestQueue(appContext);
        String url = Main.MAIN_URL + "api/teachers/all";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //парсим полученный список учителей
                Gson gson = new Gson();
                TeachersList[] teachersList = gson.fromJson(response, TeachersList[].class);

                if(teachersList.length!=0){
                    //добавляем в массив из класса Teachers учителей
                    for (int i = 0; i < teachersList.length; ++i)
                            Teachers.teachersList.put(teachersList[i].teacher_id,teachersList[i].FIO);
                }

                try {
                    methodParam.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(appContext, "No connection with our server,try later...", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(request);
    }
}
