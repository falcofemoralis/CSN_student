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
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Groups {

    /*список групп по курсу
     * Code_Group -  код группы в базе данных
     * GroupName - имя группы
     * */
    public static class GroupsList {
        public int id;
        public String GroupName;
        public Date lastUpdate;

        public class ScheduleList {
            public String subject;
            public String type;
            public int room;

            public ScheduleList(String subject, String type, int room) {
                this.room = room;
                this.subject = subject;
                this.type = type;
            }
        }
        // ScheduleList[Половина][День][Пара]
        public ScheduleList[][][] scheduleList = new GroupsList.ScheduleList[2][7][8];

        public GroupsList(int id, String GroupName, Date lastUpdate) {
            this.id = id;
            this.GroupName = GroupName;
            this.lastUpdate = lastUpdate;
        }

        public void addSchedule(int half, int day, int pair, String subject, String type, int room) {
            scheduleList[half][day][pair] = new GroupsList.ScheduleList(subject, type, room);
        }

    }
    public static ArrayList<GroupsList> groupsLists = new ArrayList<GroupsList>();

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
            String response = JSONHelper.read(context, "Groups");
            Gson gson = new Gson();
            Type listType = new TypeToken<List<GroupsList>>() {}.getType();
            instance.groupsLists = gson.fromJson(response, listType);
            SHOW_ALL();
            return instance;
        } catch (Exception ex) {
            // В случае неудачи, если данные к примеру повреждены или их просто нету - возвращает null
            return null;
        }
    }

    public static void SHOW_ALL() {
        for (int i = 0; i < groupsLists.size(); ++i) {
            groupsLists.get(i).id = groupsLists.get(i).id;
            groupsLists.get(i);
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
                try {
                    JSONArray JSONArray = new JSONArray(response);
                    for (int i = 0; i < JSONArray.length(); ++i) {
                        org.json.JSONObject JSONObject = JSONArray.getJSONObject(i);
                        int id = JSONObject.getInt("id");
                        String GroupName = JSONObject.getString("GroupName");
                        //groupsLists.add(new GroupsList(id, GroupName, new Date());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


               // Gson gson = new Gson();
               // GroupsList[] groupsLists = gson.fromJson(response, GroupsList[].class);

                //создаем лист групп
                List<String> groups = new ArrayList<String>();
                if (groupsLists.size() != 0) {
                    //добавляем в массив из класса Groups группы
                    for (int j = 0; j < groupsLists.size(); ++j)
                        groups.add(groupsLists.get(j).GroupName);
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
        String url = Main.MAIN_URL + "api/groups?Course=" + course;

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
                        String GroupName = JSONObject.getString("GroupName");
                        groupsLists.add(new GroupsList(id, GroupName, new Date()));
                        getSchedule(appContext, id, i == (JSONArray.length()  - 1) );
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

    public static void getSchedule(final Context appContext, final int id, final boolean saveData) {
        RequestQueue requestQueue = Volley.newRequestQueue(appContext);
        String url = Main.MAIN_URL + String.format("api/groups/%d/schedule", id);

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

                        groupsList.addSchedule(Integer.parseInt(half), Integer.parseInt(day) - 1, Integer.parseInt(pair) - 1, discipline, type, Integer.parseInt(room));
                    }

                    if (saveData)
                        save(appContext);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

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

    public static void save(Context appContext) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(groupsLists);
        JSONHelper.create(appContext, "Groups", jsonString);
    }

    public static GroupsList findById(int id) {
        for (int i = 0; i < groupsLists.size(); ++i)
            if (groupsLists.get(i).id == id)
                return groupsLists.get(i);
        return null;
    }

}
