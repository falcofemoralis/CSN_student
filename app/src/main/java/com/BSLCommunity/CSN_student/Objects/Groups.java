package com.BSLCommunity.CSN_student.Objects;

import android.content.Context;
import android.widget.ArrayAdapter;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class Groups {
    /*список групп по курсу
     * Code_Group -  код группы в базе данных
     * GroupName - имя группы
     * */
    public class GroupsList { public int id; public String GroupName;}

    public static GroupsList[] groupsLists;

    /* Функция получение групп по курсу и установка их в спиннер
     * Параметры:
     * appContext - application context
     * course - номер курса
     * */
    public static void getGroups(final Context appContext, int course, final Callable<Void> ... methodParam) {
        RequestQueue requestQueue = Volley.newRequestQueue(appContext);
        String url = Main.MAIN_URL + String.format("api/groups?Course=%1$s", course);

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //сохраняем группы в файл
                JSONHelper.create(appContext, Main.GROUP_FILE_NAME, response);

                //парсим полученный список групп
                Gson gson = new Gson();
                groupsLists = gson.fromJson(response, GroupsList[].class);
                
                for(int i = 0; i <  methodParam.length; ++i) {
                    try {
                        methodParam[0].call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
