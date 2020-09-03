package com.BSLCommunity.CSN_student.Objects;

import android.content.Context;
import android.widget.Toast;
import com.BSLCommunity.CSN_student.Activities.MainActivity;
import com.BSLCommunity.CSN_student.Activities.Schedule.ScheduleList;
import com.BSLCommunity.CSN_student.Managers.JSONHelper;
import com.BSLCommunity.CSN_student.R;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

public class Groups {
    static final String DATA_FILE_NAME = "Groups";

    // Функция получение групп по курсу и установка их в спиннер
    public static class GroupsList {
        public int id;
        public String GroupName;
        public Date lastUpdate;

        public ArrayList<ScheduleList> scheduleList = new ArrayList<>();

        public GroupsList(int id, String GroupName, Date lastUpdate) {
            this.id = id;
            this.GroupName = GroupName;
            this.lastUpdate = lastUpdate;
        }

        // Добавляет расписание в группу
        public void addSchedule(int half, int day, int pair, String subject, String type, String room) {
            scheduleList.add(new ScheduleList(half, day, pair, subject, type, room));
        }
    }

    public static ArrayList<GroupsList> groupsLists = new ArrayList<>();

    /* Инициализация групп (извлекается из локального файла или в противном случае выкачивается из сервера)
     * Параметры:
     * context - контекст приложения
     * course - учебный курс
     * callBack - объект реализующий интерфейс callBack, если callBack не нужен, передается null (на случай если необходимо будет скачать данные с сервера)
     */
    public static void init(Context context, int course, final Callable<Void>... callBacks) {

        if (!groupsLists.isEmpty())
            return;

        try {
            //загружаем расписание из отдельного json файла
            String response = JSONHelper.read(context, DATA_FILE_NAME);

            if (response.equals("NOT FOUND")) {
                downloadFromServer(context, course, callBacks);
                return;
            }

            Gson gson = new Gson();
            Type listType = new TypeToken<List<GroupsList>>() {
            }.getType();
            groupsLists = gson.fromJson(response, listType);
            // После скачивания всех данных вызывается callBack, у объекта который инициировал скачиввание данных с сервер, если это необходимо
            for (int i = 0; i < callBacks.length; ++i) {
                try {
                    callBacks[i].call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception ex) {
        }
    }

    /* Функция получение групп по курсу из базы
     * Параметры:
     * appContext - application context
     * course - номер курса
     * callBack - объект реализующий интерфейс callBack, если callBack не нужен, передается null
     * */
    public static void downloadFromServer(final Context appContext, int course, final Callable<Void>... callBacks) {
        RequestQueue requestQueue = Volley.newRequestQueue(appContext);
        String url = MainActivity.MAIN_URL + "api/groups?Course=" + course;

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //парсим полученный список групп
                try {
                    JSONArray JSONArray = new JSONArray(response);
                    for (int i = 0; i < JSONArray.length(); ++i) {
                        org.json.JSONObject JSONObject = JSONArray.getJSONObject(i);
                        int id = JSONObject.getInt("id");
                        String GroupName = JSONObject.getString("GroupName");
                        // Добавляем группу в список
                        groupsLists.add(new GroupsList(id, GroupName, new Date()));
                        // Скачиваем расписание группы, если все остальные группы скачаны, то после скачивания последней - сохраняем данные
                        getSchedule(appContext, id, i == (JSONArray.length() - 1), callBacks);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(appContext, R.string.no_connection_server, Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(request);
    }

    /* Функция загруки расписания из базы для группы
     * Параметры:
     * appContext - контекст приложения
     * id - id группы
     * saveData - сохранять данные или нет ? true - сохранить, false - не сохранять
     * callBack - объект реализующий интерфейс callBack, если callBack не нужен, передается null
     * */
    public static void getSchedule(final Context appContext, final int id, final boolean saveData, final Callable<Void>... callBacks) {
        RequestQueue requestQueue = Volley.newRequestQueue(appContext);
        String url = MainActivity.MAIN_URL + String.format("api/groups/%d/schedule", id);

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //парсим полученный список групп
                try {
                    JSONArray JSONArray = new JSONArray(response);
                    GroupsList groupsList = Groups.findById(id);
                    //парсим расписание
                    for (int i = 0; i < JSONArray.length(); ++i) {
                        org.json.JSONObject dayJSONObject = JSONArray.getJSONObject(i);
                        String day = dayJSONObject.getString("Day");
                        String half = dayJSONObject.getString("Half");
                        String pair = dayJSONObject.getString("Pair");
                        String discipline = dayJSONObject.getString("NameDiscipline");
                        String room = dayJSONObject.getString("Room");
                        String type = dayJSONObject.getString("SubjectType");

                        // Добавляем расписание в список
                        groupsList.addSchedule(Integer.parseInt(half), Integer.parseInt(day) - 1, Integer.parseInt(pair) - 1, discipline, type, room);
                    }

                    // После скачивания всез данныз вызывается callBack, у объекта который инициировал скачиввание данных с сервер, если это необходимо
                    if (callBacks != null) {
                        for (int i = 0; i < callBacks.length; ++i) {
                            try {
                                callBacks[i].call();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    // Сохранение данных если это необходимо
                    if (saveData)
                        save(appContext);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(appContext, R.string.no_connection_server, Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(request);
    }

    // Сохраняет данные о группах в Json файл
    public static void save(Context appContext) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(groupsLists);
        JSONHelper.create(appContext, DATA_FILE_NAME, jsonString);
    }

    // Поиск группы по id
    public static GroupsList findById(int id) {
        for (int i = 0; i < groupsLists.size(); ++i)
            if (groupsLists.get(i).id == id)
                return groupsLists.get(i);
        return null;
    }

    public static void delete(final Context appContext) {
        JSONHelper.delete(appContext, DATA_FILE_NAME);
        groupsLists.clear();
    }

}
