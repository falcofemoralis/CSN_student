package com.BSLCommunity.CSN_student.Models;

import android.content.SharedPreferences;
import android.util.Pair;

import java.util.ArrayList;

public class Settings {

    // Строковые константы - ключи, по которым хранятся все данные в файле настроек
    public enum PrefKeys {
        USER_ID("user_id"),
        NICKNAME("nickname"),
        PASSWORD("password"),
        GROUP("group"),
        GROUP_ID("group_id"),
        COURSE("course"),
        TOKEN("token"),
        OFFLINE_DATA("offline_data"),
        TIMER_SWITCH("timer_switch"),
        LANGUAGE("language");

        private String value;
        private PrefKeys(String value) {
            this.value = value;
        }

        public String getKey() {
            return value;
        }
    }
    public static ArrayList<Pair<String, String> > languages = new ArrayList<>(); //название языков, где пара <название языка, его код);
    public static SharedPreferences encryptedSharedPreferences;
}
