package com.BSLCommunity.CSN_student.Objects;

import android.content.Context;
import android.widget.Button;
import android.widget.Toast;

import com.BSLCommunity.CSN_student.Activities.Main;
import com.BSLCommunity.CSN_student.Managers.JSONHelper;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SubjectsInfo {
    public static SubjectsInfo instance = null;
    public static final String FILE_NAME = "subjectsInfo";

    public static SubjectsInfo getInstance(Context context) {
        if (instance == null)
            return init(context);
        return instance;
    }

    private static SubjectsInfo init(Context context) {
        try {
            // Извлечение локальных данных пользователя
            instance = new SubjectsInfo();

            //загрузка данных
            String JSONstring = JSONHelper.read(context, FILE_NAME);
            if (JSONstring != null && !JSONstring.equals("") && !JSONstring.equals("NOT FOUND")) {
                instance.subjectInfo = new SubjectInfo[Subjects.subjectsList.length];
                for (int i = 0; i < Subjects.subjectsList.length; ++i)
                    instance.subjectInfo[i] = new SubjectInfo();

                Gson gson = new Gson();
                instance.subjectInfo = gson.fromJson(JSONstring, SubjectInfo[].class);
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

    public static class SubjectInfo {
        public int subjectValue;
        public int labsCount, ihwCount, otherCount;
        public int[] labValue, ihwValue, otherValue;
        public String[] labName, ihwName, otherName;
    }

    public SubjectInfo[] subjectInfo;

    //сохраняем кол-во лаб по дисциплине
    public void saveCount(int subjectId, int labsCount, int ihwCount, int otherCount) {
        instance.subjectInfo[subjectId].labsCount = labsCount;
        instance.subjectInfo[subjectId].ihwCount = ihwCount;
        instance.subjectInfo[subjectId].otherCount = otherCount;
    }

    //сохраняем ценность предмета
    public void saveSubjectValue(int subjectId, int id) {
        instance.subjectInfo[subjectId].subjectValue = id;
    }

    public void saveData(int subjectId,
                         ArrayList<Integer> labValues, int labsCount,
                         ArrayList<Integer> ihwValues, int ihwCount,
                         ArrayList<Integer> otherValues, int otherCount,
                         ArrayList<Button> labNames,
                         ArrayList<Button> ihwNames,
                         ArrayList<Button> otherNames
    ) {

        SubjectInfo subjectInfo = instance.subjectInfo[subjectId];
        subjectInfo.labValue = new int[labsCount];
        subjectInfo.labName = new String[labsCount];

        for (int i = 0; i < labsCount; ++i) {
            subjectInfo.labValue[i] = labValues.get(i);
            subjectInfo.labName[i] = labNames.get(i).getText().toString();
        }

        subjectInfo.ihwValue = new int[ihwCount];
        subjectInfo.ihwName = new String[ihwCount];
        for (int i = 0; i < ihwCount; ++i) {
            subjectInfo.ihwValue[i] = ihwValues.get(i);
            subjectInfo.ihwName[i] = ihwNames.get(i).getText().toString();
        }

        subjectInfo.otherValue = new int[otherCount];
        subjectInfo.otherName = new String[otherCount];
        for (int i = 0; i < otherCount; ++i) {
            subjectInfo.otherValue[i] = otherValues.get(i);
            subjectInfo.otherName[i] = otherNames.get(i).getText().toString();
        }

    }

    //сохраням данный в JSON файл
    public void saveSubject(Context context) throws JSONException {
        Gson gson = new Gson();
        String jsonString = gson.toJson(subjectInfo);
        JSONHelper.create(context, FILE_NAME, jsonString);
        updateRating(context);
    }

    public static void updateRating(final Context context) throws JSONException {
        final String JSONString = JSONHelper.read(context, FILE_NAME);

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = Main.MAIN_URL + String.format("api/users/%1$s/rating", User.getInstance().id);
        JsonArrayRequest request = new JsonArrayRequest
                (Request.Method.PUT, url, new JSONArray(JSONString), new Response.Listener<JSONArray>() {
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

        String url = Main.MAIN_URL + String.format("api/users/%1$s/rating", User.getInstance().id);
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
