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
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

public class Teachers {

    static final String DATA_FILE_NAME = "Teachers";

    public static class TeacherList {
        public int id;
        public String FIO;
        public Date lastUpdate;

        public class ScheduleList {
            public int half, day, pair;
            public String subject;
            public String type;
            public int room;

            public ScheduleList(int half, int day, int pair, String subject, String type, int room) {
                this.half = half;
                this.day = day;
                this.pair = pair;
                this.room = room;
                this.subject = subject;
                this.type = type;
            }
        }

        // ScheduleList[Половина][День][Пара]
        public ArrayList<Teachers.TeacherList.ScheduleList> scheduleList = new ArrayList<>();

        public TeacherList(int id, String FIO, Date lastUpdate) {
            this.id = id;
            this.FIO = FIO;
            this.lastUpdate = lastUpdate;
        }

        // Добавляет расписание в группу
        public void addSchedule(int half, int day, int pair, String subject, String type, int room) {
            scheduleList.add(new Teachers.TeacherList.ScheduleList(half, day, pair, subject, type, room));
        }

    }

    public static ArrayList<Teachers.TeacherList> teacherLists = new ArrayList<>();

    public static void init(Context context, final Callable<Void>... callBacks) {

        try {
            //загружаем расписание из отдельного json файла
            String response = JSONHelper.read(context, DATA_FILE_NAME);

            if (response.equals("NOT FOUND")) {
                downloadFromServer(context, callBacks);
                return;
            }

            Gson gson = new Gson();
            Type listType = new TypeToken<List<Teachers.TeacherList>>() {
            }.getType();
            teacherLists = gson.fromJson(response, listType);

            // После скачивания всех данных вызывается callBack, у объекта который инициировал скачиввание данных с сервер, если это необходимо
            for (int i = 0; i < callBacks.length; ++i) {
                try {
                    callBacks[i].call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception ex) {
            // В случае неудачи, если данные к примеру повреждены или их просто нету - возвращает null
            return;
        }
    }

    /* Функция получение учителей в университете
     * Параметры:
     * appContext - application context
     * */
    public static void downloadFromServer(final Context appContext, final Callable<Void>... callBacks) {
        RequestQueue requestQueue = Volley.newRequestQueue(appContext);
        String url = Main.MAIN_URL + "api/teachers/all";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //сохраняем группы в файл
                JSONHelper.create(appContext, Main.GROUP_FILE_NAME, response);

                //парсим полученный список групп
                try {
                    JSONArray JSONArray = new JSONArray(response);
                    for (int i = 0; i < JSONArray.length(); ++i) {
                        org.json.JSONObject JSONObject = JSONArray.getJSONObject(i);
                        int id = JSONObject.getInt("id");
                        String FIO = JSONObject.getString("FIO");
                        // Добавляем учителя в список
                        teacherLists.add(new TeacherList(id, FIO, new Date()));
                        // Скачиваем расписание учителя, если все остальные учителя скачаны, то после скачивания последнего - сохраняем данные
                        getSchedule(appContext, id, i == (JSONArray.length() - 1), callBacks);
                    }
                } catch (JSONException e) {
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

    /* Функция загруки расписания из базы для группы
     * Параметры:
     * appContext - контекст приложения
     * id - id учителя
     * saveData - сохранять данные или нет ? true - сохранить, false - не сохранять
     * callBack - объект реализующий интерфейс callBack, если callBack не нужен, передается null
     * */
    public static void getSchedule(final Context appContext, final int id, final boolean saveData, final Callable<Void>... callBacks) {
        RequestQueue requestQueue = Volley.newRequestQueue(appContext);
        String url = Main.MAIN_URL + String.format("api/teachers/%d/schedule", id);

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //парсим полученный список групп
                try {
                    JSONArray JSONArray = new JSONArray(response);
                    Teachers.TeacherList teacherList = Teachers.findById(id);
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
                        teacherList.addSchedule(Integer.parseInt(half), Integer.parseInt(day) - 1, Integer.parseInt(pair) - 1, discipline, type, Integer.parseInt(room));
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
                Toast.makeText(appContext, "No connection with our server,try later...", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(request);
    }

    // Сохраняет данные об учителях в Json файл
    public static void save(Context appContext) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(teacherLists);
        JSONHelper.create(appContext, DATA_FILE_NAME, jsonString);
    }

    // Поиск учителя по id
    public static Teachers.TeacherList findById(int id) {
        for (int i = 0; i < teacherLists.size(); ++i)
            if (teacherLists.get(i).id == id)
                return teacherLists.get(i);
        return null;
    }
}
