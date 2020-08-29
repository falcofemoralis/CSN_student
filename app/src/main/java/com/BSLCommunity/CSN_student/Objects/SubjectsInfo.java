package com.BSLCommunity.CSN_student.Objects;

import android.content.Context;
import android.widget.Toast;

import com.BSLCommunity.CSN_student.Activities.MainActivity;
import com.BSLCommunity.CSN_student.Managers.JSONHelper;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SubjectsInfo {
    public static SubjectsInfo instance = null;
    public static final String FILE_NAME = "subjectsInfo";

    public static class SubjectInfo {
        public int subjectValue;

        public class Work {
            public int count; // Количество работ
            public ArrayList<Integer> values; // Статус выполнения каждой работы
            public ArrayList<String> names; // Название каждой работы
            public ArrayList<Integer> marks; // Оценка за каждую работу

            public Work() {
                values = new ArrayList<Integer>();
                names = new ArrayList<String>();
                marks = new ArrayList<Integer>();
            }

            // Добавление пустого объекта
            public void addWork() {
                ++count;
                values.add(0);
                names.add("");
                marks.add(0);
            }

            // Удаление элемента по индексу
            public void deleteWork(int index) {
                --count;
                values.remove(index);
                names.remove(index);
                marks.remove(index);
            }
        }
        public Work labs, ihw, others;

        public SubjectInfo() {
            labs = new Work();
            ihw = new Work();
            others = new Work();
        }

        public int calculateProgress() {
            final int COMPLETE = 6;

            int subjectComplete = 0;

            // Считаем сколько он выполнил лабораторных, ИДЗ, других дел
            for (int i = 0; i < labs.count; ++i)
                if (labs.values.get(i) == COMPLETE) subjectComplete++;

            for (int i = 0; i < ihw.count; ++i)
                if (ihw.values.get(i) == COMPLETE) subjectComplete++;

            for (int i = 0; i < others.count; ++i)
                if (others.values.get(i) == COMPLETE) subjectComplete++;

            int sumCount = (labs.count + ihw.count + others.count);
            return sumCount > 0 ? 100 * subjectComplete / sumCount : 0;
        }
    }

    public static SubjectsInfo getInstance(Context context) {
        if (instance == null)
            return init(context);
        return instance;
    }
    public SubjectInfo[] subjectInfo;

    private static SubjectsInfo init(Context context) {
        try {
            // Извлечение локальных данных пользователя
            instance = new SubjectsInfo();

            instance.subjectInfo = new SubjectInfo[Subjects.subjectsList.length];
            for (int i = 0; i < Subjects.subjectsList.length; ++i)
                instance.subjectInfo[i] = new SubjectInfo();

            //загрузка данных
            String JSONString = JSONHelper.read(context, FILE_NAME);
            if (!JSONString.equals("NOT FOUND")) {
                Gson gson = new Gson();
                instance.subjectInfo = gson.fromJson(JSONString, SubjectInfo[].class);
            }

            return instance;
        } catch (Exception e) {
            // В случае неудачи, если данные к примеру повреждены или их просто нету - возвращает null
            return null;
        }
    }

    public static void deleteSubjects(Context context) {
        JSONHelper.delete(context, FILE_NAME);
        instance = null;
    }

    //сохраням данный в JSON файл
    public void save(Context context) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(subjectInfo);
        JSONHelper.create(context, FILE_NAME, jsonString);
        try {
            updateRating(context);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void updateRating(final Context context) throws JSONException {
        final String JSONString = JSONHelper.read(context, FILE_NAME);

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = MainActivity.MAIN_URL + String.format("api/users/%1$s/rating", User.getInstance().id);
        JsonArrayRequest request = new JsonArrayRequest (Request.Method.PUT, url, new JSONArray(JSONString), new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                return params;
            }
        };
        requestQueue.add(request);
    }

    public static void downloadRating(final Context context) throws JSONException {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        String url = MainActivity.MAIN_URL + String.format("api/users/%1$s/rating", User.getInstance().id);
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null && !response.equals("")) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String rating = jsonObject.getString("JSON_RATING");
                        JSONHelper.create(context,FILE_NAME, rating);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "No connection with our server,try later...", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(request);
    }
}
