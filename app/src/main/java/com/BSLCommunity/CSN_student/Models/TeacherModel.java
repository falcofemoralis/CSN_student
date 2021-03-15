package com.BSLCommunity.CSN_student.Models;

import android.util.Log;

import com.BSLCommunity.CSN_student.APIs.TeacherApi;
import com.BSLCommunity.CSN_student.Managers.FileManager;
import com.BSLCommunity.CSN_student.R;
import com.BSLCommunity.CSN_student.lib.ExCallable;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TeacherModel {
    public static final String DATA_FILE_NAME = "Teachers"; // Файл для сохранения данных про преподователей
    public static TeacherModel instance = null;

    public static class Teacher {
        @SerializedName("Code_Teacher")
        public int id;
        @SerializedName("FIO")
        public String FIO;

        public ArrayList<ScheduleList> scheduleList = new ArrayList<>();

        public Teacher(int id, String FIO) {
            this.id = id;
            this.FIO = FIO;
        }
    }

    public static ArrayList<TeacherModel.Teacher> teachers;

    private Retrofit retrofit;

    private TeacherModel() {
    }

    public static TeacherModel getTeacherModel() {
        if (instance == null) {
            instance = new TeacherModel();
            instance.init();
        }
        return instance;
    }

    /**
     * Инциализция преподователей из файла
     */
    public void init() {
        this.retrofit = new Retrofit.Builder()
                .baseUrl(TeacherApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        try {
            String data = FileManager.readFile(DATA_FILE_NAME);
            Type type = new TypeToken<ArrayList<TeacherModel.Teacher>>() {
            }.getType();
            teachers = (new Gson()).fromJson(data, type);
        } catch (Exception e) {
            teachers = new ArrayList<>();
        }
    }

    /**
     * Скачивание всех преподователей
     *
     * @param exCallable - колбек
     */
    public Thread getAllTeachers(final ExCallable<ArrayList<TeacherModel.Teacher>> exCallable) {
        return (new Thread() {
            @Override
            public void run() {
                if (!teachers.isEmpty()) {
                    exCallable.call(teachers);
                    return;
                }

                TeacherApi teacherApi = retrofit.create(TeacherApi.class);
                Call<ArrayList<TeacherModel.Teacher>> call = teacherApi.allTeachers();
                try {
                    teachers = call.execute().body();
                    save();
                    exCallable.call(teachers);
                } catch (IOException e) {
                    exCallable.fail(R.string.no_connection_server);
                    Log.d("ERROR_API", e.toString());
                }
            }
        });
    }

    /**
     * Загрузка рассписания для учителей
     */
    public Thread loadSchedule(final ExCallable<Integer> exCallable) {
        return (new Thread() {
            @Override
            public void run() {
                TeacherApi teacherApi = retrofit.create(TeacherApi.class);

                for (final Teacher teacher : teachers) {
                    if (!DataModel.isFailed) {
                        Call<ArrayList<ScheduleList>> call = teacherApi.scheduleByTeacherId(teacher.id);

                        try {
                            teacher.scheduleList = call.execute().body();
                            save();
                            Log.d("CACHE_API", "downloaded schedule for teacher: " + teacher.FIO);
                            exCallable.call(-1);
                        } catch (IOException e) {
                            Log.d("ERROR_API", e.toString());
                            exCallable.fail(-1);
                        }
                    }
                }
            }
        });
    }

    /**
     * Сохранение данных в файл
     */
    public void save() {
        String data = (new Gson()).toJson(teachers);
        try {
            FileManager.writeFile(DATA_FILE_NAME, data, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Поиск преподователя по id
     *
     * @param id - id преподователя
     * @return найденный преподователь или null если он не существет
     */
    public TeacherModel.Teacher findById(int id) {
        for (int i = 0; i < teachers.size(); ++i)
            if (teachers.get(i).id == id)
                return teachers.get(i);
        return null;
    }

    /**
     * Поиск преподователя по его ФИО
     *
     * @param FIO - ФИО преподователя
     * @return найденный преподователь или null если он не существет
     */
    public TeacherModel.Teacher findByName(String FIO, String lang) throws JSONException {
        for (int i = 0; i < teachers.size(); ++i)
            if ((new JSONObject(teachers.get(i).FIO)).getString(lang).equals(FIO))
                return teachers.get(i);

        return null;
    }

    /**
     * Получение всех преподователей
     *
     * @return список состоящий из ФИО преподователей
     */
    public ArrayList<String> getTeachersNames() {
        ArrayList<String> names = new ArrayList<>();

        for (Teacher teacher : teachers) {
            names.add(teacher.FIO);
        }
        return names;
    }
}
