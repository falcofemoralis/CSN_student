package com.BSLCommunity.CSN_student.Objects;

import android.content.Context;

import com.BSLCommunity.CSN_student.Activities.Main;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.Callable;

public class LocalData {

    // Константы типо объектов хранящихся в локальном хранилище
    public enum TypeData {
        groups,
        teachers
    }

    // Хранит информацию последнего изменения группы по id
    public static HashMap<Integer, Date> updateListGroups = new HashMap<Integer, Date>();
    public static HashMap<Integer, Date> updateListTeachers = new HashMap<Integer, Date>();

    // Загрзка Апдейта списка групп (список который хранит время изменения каждой группы на сервере)
    public static void downloadUpdateList(final Context appContext, final HashMap<Integer, Date> updateList, final TypeData entity, final Callable<Void> ... callBacks) {
        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(appContext);

        String url = Main.MAIN_URL + String.format("api/%s/updateList", entity.toString());

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d H:m:s");
                    JSONArray JSONArray = new JSONArray(response);
                    for (int i = 0; i < JSONArray.length(); ++i) {
                        org.json.JSONObject JSONObject = JSONArray.getJSONObject(i);
                        int id = JSONObject.getInt("id");
                        String date = JSONObject.getString("lastUpdate");

                        // Преобразование строки в дату
                        Date lastUpdate;
                        try {
                            lastUpdate = sdf.parse(date);
                            updateList.put(id, lastUpdate);
                        } catch (Exception e) { }
                    }

                    if (callBacks != null) {
                        // Вызов всех callBacks
                        for (int i = 0; i < callBacks.length; ++i)
                            try {
                                callBacks[i].call();
                            } catch (Exception e) { }
                    }
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue.add(request);
    }



    /* Проверка нуждаются ли данные в обновлении
    * Параметры:
    * appContext - контекст всего приложения
    * type - тип объекта (учитель или группа)
    * fileDate - Время последнего изменения файла
    * */
    public static void checkUpdate(final Context appContext, final TypeData type) {

        // Выбор действия в зависимости от типа данных
        switch (type) {
            case groups:
                // Проверяем актуальность данных в группах и скачиваем новые по необходимости
                for (int i = 0; i < Groups.groupsLists.size(); ++i) {
                    Groups.GroupsList localGroup = Groups.groupsLists.get(i);
                    if (localGroup.lastUpdate.before(updateListGroups.get(localGroup.id)))
                        Groups.getSchedule(appContext, localGroup.id, true);
                }
                break;
            case teachers:
                // Проверяем актуальность данных об учителях и скачиваем новые по необходимости
                for (int i = 0; i < Teachers.teacherLists.size(); ++i) {
                    Teachers.TeacherList localTeacher = Teachers.teacherLists.get(i);
                    if (localTeacher.lastUpdate.before(updateListTeachers.get(localTeacher.id)))
                        Teachers.downloadScheduleFromServer(appContext, localTeacher.id, true);
                }
                break;
        }
    }

}
