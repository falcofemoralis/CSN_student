package com.BSLCommunity.CSN_student.Models;

import android.util.Log;

import com.BSLCommunity.CSN_student.APIs.AchievementsApi;
import com.BSLCommunity.CSN_student.App;
import com.BSLCommunity.CSN_student.Managers.FileManager;
import com.BSLCommunity.CSN_student.R;
import com.BSLCommunity.CSN_student.lib.ExCallable;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AchievementsModel {
    public static class UserAchievement {
        public int id;
        public boolean isCompleted;

        public UserAchievement() {
        }

        public UserAchievement(int id, boolean isCompleted) {
            this.id = id;
            this.isCompleted = isCompleted;
        }
    }

    public static class Achievement {
        public int id;
        public String name;
        public String info;
    }

    private final String FILE_NAME = "achievements";
    public ArrayList<Achievement> achievements;
    private Retrofit retrofit;
    public static AchievementsModel instance;

    private AchievementsModel() {
    }

    public static AchievementsModel getAchievementsModel() {
        if (instance == null) {
            instance = new AchievementsModel();
            instance.init();
        }
        return instance;
    }

    public void init() {
        this.retrofit = new Retrofit.Builder()
                .baseUrl(AchievementsApi.RESERVE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        try {
            String data = FileManager.getFileManager(App.getApp().context()).readFile(FILE_NAME);
            Type type = new TypeToken<ArrayList<Achievement>>() {
            }.getType();
            achievements = (new Gson()).fromJson(data, type);
        } catch (Exception e) {
            achievements = new ArrayList<>();
        }
    }

    public void getAchievements(final ExCallable<Void> exCallable) {
        AchievementsApi subjectApi = retrofit.create(AchievementsApi.class);
        Call<ArrayList<Achievement>> call = subjectApi.getAchievements();
        call.enqueue(new Callback<ArrayList<Achievement>>() {
            @Override
            public void onResponse(@NotNull Call<ArrayList<Achievement>> call, @NotNull retrofit2.Response<ArrayList<Achievement>> response) {
                achievements = response.body();
                save();
            }

            @Override
            public void onFailure(Call<ArrayList<Achievement>> call, Throwable t) {
                exCallable.fail(R.string.no_connection_server);
                Log.d("ERROR_API", t.toString());
            }
        });
    }

    public void save() {
        String data = (new Gson()).toJson(achievements);
        try {
            FileManager.getFileManager(App.getApp().context()).writeFile(FILE_NAME, data, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
