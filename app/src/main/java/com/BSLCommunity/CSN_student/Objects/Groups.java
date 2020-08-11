package com.BSLCommunity.CSN_student.Objects;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.BSLCommunity.CSN_student.Activities.Main;
import com.BSLCommunity.CSN_student.Activities.Schedule;
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

import java.util.ArrayList;
import java.util.List;

public class Groups {

    /*список групп по курсу
     * Code_Group -  код группы в базе данных
     * GroupName - имя группы
     * */
    public class GroupsList { public int id; public String GroupName;}
    public GroupsList[] groupsLists;

    //реализация синглтона
    public static Groups instance = null;
    public static Groups getInstance(Context context) {
        if (instance == null)
            return init(context);
        return instance;
    }

    //удаление списка групп
    public static void deleteGroups() {
        instance = null;
    }

    /* Инициализация групп (извлекается из локального файла)
     * В случае неудачи будет возвращен null как признак того что группы не был создан
     */
    private static Groups init(Context context) {

        try {
            // Извлечение локальных данных пользователя
            instance = new Groups();

            //загружаем расписание из отдельного json файла
            String response = JSONHelper.read(context, Main.GROUP_FILE_NAME);
            Gson gson = new Gson();

            instance.groupsLists = gson.fromJson(response, GroupsList[].class);
            return instance;
        } catch (Exception ex) {
            // В случае неудачи, если данные к примеру повреждены или их просто нету - возвращает null
            return null;
        }
    }


    /* Функция получение групп по курсу и установка их в спиннер
     * Параметры:
     * appContext - application context
     * course - номер курса
     * */
    public static void getGroups(final Context appContext, int course, final Spinner groupSpinner, final int spinnerLayout) {
        RequestQueue requestQueue = Volley.newRequestQueue(appContext);
        String url = Main.MAIN_URL + String.format("api/groups?Course=%1$s", course);

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //сохраняем группы в файл
                JSONHelper.create(appContext, Main.GROUP_FILE_NAME, response);

                //парсим полученный список групп
                Gson gson = new Gson();
                GroupsList[] groupsLists = gson.fromJson(response, GroupsList[].class);

                //создаем лист групп
                List<String> groups = new ArrayList<String>();
                if (groupsLists.length != 0) {
                    //добавляем в массив из класса Groups группы
                    for (int j = 0; j < groupsLists.length; ++j)
                        groups.add(groupsLists[j].GroupName);
                } else {
                    //в том случае если групп по курсу нету
                    groups.add("No groups");
                }

                //устанавливаем спинер выбора групп
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(appContext, spinnerLayout, groups);
                dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_schedule);
                groupSpinner.setAdapter(dataAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(appContext, "No connection with our server,try later...", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(request);
    }

    /* Функция получение групп по курсу
     * Параметры:
     * appContext - application context
     * course - номер курса
     * */
    public static void getGroups(final Context appContext, int course) {
        RequestQueue requestQueue = Volley.newRequestQueue(appContext);
        String url = Main.MAIN_URL + String.format("api/groups?Course=%1$s", course);

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //сохраняем группы в файл
                JSONHelper.create(appContext, Main.GROUP_FILE_NAME, response);
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
