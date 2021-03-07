package com.BSLCommunity.CSN_student.Models;

import android.content.Context;
import android.widget.Toast;

import com.BSLCommunity.CSN_student.Managers.DBHelper;
import com.BSLCommunity.CSN_student.Managers.JSONHelper;
import com.BSLCommunity.CSN_student.R;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
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

    public static int[] colors = { //цвета кнопок выбора
            R.color.not_passed,
            R.color.in_process,
            R.color.done_without_report,
            R.color.done_with_report,
            R.color.waiting_acceptation,
            R.color.passed_without_report,
            R.color.passed_with_report};

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
            System.out.println(e.toString());
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

        if (subjectInfo != null) {
            Gson gson = new Gson();
            String jsonString = gson.toJson(subjectInfo);
            JSONHelper.create(context, FILE_NAME, jsonString);
            try {
                updateRating(context, jsonString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //TODO
    public static void updateRating(final Context context, final String JSONString) throws JSONException {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = DBHelper.MAIN_URL + String.format("api/users/%1$s/rating", 0);
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
        String apiUrl = String.format("api/users/%1$s/rating", 0);
        DBHelper.getRequest(context, apiUrl, DBHelper.TypeRequest.STRING, new DBHelper.CallBack<String>(){

            @Override
            public void call(String response) {
                if (response != null && !response.equals("")) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String rating = jsonObject.getString("JSON_RATING");
                        if(!rating.equals("0")){
                            Gson gson = new Gson();
                            instance = new SubjectsInfo();
                            instance.subjectInfo = gson.fromJson(rating, SubjectInfo[].class);
                            JSONHelper.create(context, FILE_NAME, rating);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void fail(String message) {
                Toast.makeText(context, R.string.no_connection_server, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
