package com.BSLCommunity.CSN_student.Objects;

import android.content.Context;;
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
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class Subjects {
    //////////////////////////////////////////////////////////////////////
    ///////////////          реализация синглтона          ///////////////
    //////////////////////////////////////////////////////////////////////
    public static Subjects instance = null;

    public static Subjects getInstance(Context context) {
        if (instance == null)
            return init(context);
        return instance;
    }

    private static Subjects init(Context context) {
        try {
            // Извлечение локальных данных пользователя
            instance = new Subjects();

            instance.subjectInfo = new SubjectInfo[subjectsList.length];
            for(int i=0; i<subjectsList.length;++i)
                instance.subjectInfo[i] = new SubjectInfo();

            //загрузка данных
            String JSONstring = JSONHelper.read(context, "subjectsInfo");
            if (JSONstring != null) {
                Gson gson = new Gson();
                instance.subjectInfo = gson.fromJson(JSONstring, SubjectInfo[].class);
            }
            return instance;
        } catch (Exception e) {
            // В случае неудачи, если данные к примеру повреждены или их просто нету - возвращает null
            return null;
        }
    }

    public static void deleteSubjects() {
        instance = null;
    }

    //////////////////////////////////////////////////////////////////////
    ///////////////             Список предметов           ///////////////
    //////////////////////////////////////////////////////////////////////

    public class SubjectsList {
        public int Code_Discipline, Code_Lector, Code_Practice, Code_Assistant;
        public String NameDiscipline;
    }
    public static SubjectsList[] subjectsList;

    public static void getSubjectsList(final Context context, final Callable<Void> methodParam) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = Main.MAIN_URL + String.format("api/subjects/?Course=%1$s", 2);

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                subjectsList = gson.fromJson(response, SubjectsList[].class);
                try {
                    methodParam.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "No connection with our server,try later...", Toast.LENGTH_SHORT).show();

                try {
                    String response = JSONHelper.read(context, Main.GROUP_FILE_NAME);
                    Gson gson = new Gson();
                    subjectsList = gson.fromJson(response, SubjectsList[].class);
                    try {
                        methodParam.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {

                }
            }
        });
        requestQueue.add(request);
    }

    //////////////////////////////////////////////////////////////////////
    ///////////////             Информация предмета        ///////////////
    //////////////////////////////////////////////////////////////////////

    public static class SubjectInfo {
        public int labsCount;
        public int subjectValue;
        public int[] labValue;
    }
    public SubjectInfo[] subjectInfo;

    //сохраняем кол-во лаб по дисциплине
    public void saveLabCount(Context context, int subjectId, int labsCount) {
        instance.subjectInfo[subjectId-1].labsCount = labsCount;
    }

    //сохраняем ценность предмета
    public void saveSubjectValue(Context context, int subjectId, int id) {
        instance.subjectInfo[subjectId-1].subjectValue = id;
    }

    public void saveLabValue(Context context, int subjectId, ArrayList<Integer> labValues, int labsCount){
        instance.subjectInfo[subjectId-1].labValue = new int[labsCount];
        for (int i=0;i<labsCount;++i)
            instance.subjectInfo[subjectId-1].labValue[i] = labValues.get(i);
    }

    //получем кол-во лаб
    public int getLabCount(int subjectId) { ;
        return instance.subjectInfo[subjectId-1].labsCount;
    }

    //получем ценноть предмета
    public int getSubjectValue(int subjectId) {
        return instance.subjectInfo[subjectId-1].subjectValue;
    }

    //сохраням данный в JSON файл
    public void saveSubject(Context context) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(subjectInfo);
        JSONHelper.create(context, "subjectsInfo", jsonString);
    }
}
