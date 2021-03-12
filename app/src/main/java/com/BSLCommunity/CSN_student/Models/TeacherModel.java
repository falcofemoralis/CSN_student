package com.BSLCommunity.CSN_student.Models;

import android.util.Log;

import com.BSLCommunity.CSN_student.APIs.TeacherApi;
import com.BSLCommunity.CSN_student.Managers.FileManager;
import com.BSLCommunity.CSN_student.R;
import com.BSLCommunity.CSN_student.lib.ExCallable;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
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
    public void getAllTeachers(final ExCallable<ArrayList<TeacherModel.Teacher>> exCallable) {
        if (!teachers.isEmpty()) {
            exCallable.call(teachers);
            return;
        }

        TeacherApi teacherApi = retrofit.create(TeacherApi.class);
        Call<ArrayList<TeacherModel.Teacher>> call = teacherApi.allTeachers();

        call.enqueue(new Callback<ArrayList<TeacherModel.Teacher>>() {
            @Override
            public void onResponse(@NotNull Call<ArrayList<TeacherModel.Teacher>> call, @NotNull retrofit2.Response<ArrayList<TeacherModel.Teacher>> response) {
                teachers = response.body();
                exCallable.call(teachers);
                save();
                Log.d("DEBUG_API", teachers.toString());
                for (TeacherModel.Teacher teacher : teachers) {
                    loadSchedule(teacher.id);
                }
            }

            @Override
            public void onFailure(@NotNull Call<ArrayList<TeacherModel.Teacher>> call, @NotNull Throwable t) {
                exCallable.fail(R.string.no_connection_server);
                Log.d("ERROR_API", t.toString());
            }
        });
    }

    /**
     * Загрузка рассписания по id
     *
     * @param teacherId - id преподавателя для которой необходимо рассписание
     */
    public void loadSchedule(final int teacherId) {
        final TeacherModel.Teacher teacher = this.findById(teacherId);

        TeacherApi teacherApi = retrofit.create(TeacherApi.class);
        Call<ArrayList<ScheduleList>> call = teacherApi.scheduleByTeacherId(teacherId);
        call.enqueue(new Callback<ArrayList<ScheduleList>>() {
            @Override
            public void onResponse(@NotNull Call<ArrayList<ScheduleList>> call, @NotNull retrofit2.Response<ArrayList<ScheduleList>> response) {
                teacher.scheduleList = response.body();
                save();
            }

            @Override
            public void onFailure(@NotNull Call<ArrayList<ScheduleList>> call, @NotNull Throwable t) {
                Log.d("ERROR_API", t.toString());
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
