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

public class LocalData {

    // Константы типо объектов хранящихся в локальном хранилище
    public static enum TypeData {
        groups,
        teachers
    }

    // Хранит информацию последнего изменения группы по id
    public static HashMap<Integer, Date> updateListGroups = new HashMap<Integer, Date>();
    public static HashMap<Integer, Date> updateListTeachers = new HashMap<Integer, Date>();

    // Загрзка Апдейта списка групп (список который хранит время изменения каждой группы на сервере)
    public static void downloadUpdateList(final Context appContext, final HashMap<Integer, Date> updateList, final TypeData entity) {
        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(appContext);

        String url = Main.MAIN_URL + entity.toString() + "/updateList";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
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
                            updateList.put(id,  lastUpdate);
                        } catch (Exception e) {}
                    }

                } catch (JSONException e) {
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
    public static void checkUpdate(Context appContext, TypeData type, Date fileDate) {

        // Выбор действия в зависимости от типа данных
        switch (type)
        {
            case groups:
                if (updateListGroups.isEmpty())
                    downloadUpdateList(appContext, updateListGroups, type);

                // Проверяем актуальность данных в группах
                for (int i = 0; i < Groups.groupsLists.size(); ++i) {
                    Groups.GroupsList localGroup = Groups.groupsLists.get(i);
                    if (localGroup.lastUpdate.before(updateListGroups.get(localGroup.id)))
                        Groups.getSchedule(appContext, localGroup.id, true);
                }
                break;
            case teachers:
                if (updateListTeachers.isEmpty())
                    downloadUpdateList(appContext, updateListTeachers, type);

                break;
        }
    }

}
