package com.BSLCommunity.CSN_student.Models;

import android.content.Context;

import com.BSLCommunity.CSN_student.Managers.JSONHelper;
import com.google.gson.Gson;

import java.io.IOException;

public class AchievementsModel {
    public static class Achievement {
        public String name;
        public String info;
        public boolean completed;
    }

    public AchievementsModel.Achievement[] achievements;

    public AchievementsModel(Context context) {
        try {
            String achievementsJson = JSONHelper.loadJSONFromAsset(context, "achievements.json");
            achievements = (new Gson()).fromJson(achievementsJson, AchievementsModel.Achievement[].class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
