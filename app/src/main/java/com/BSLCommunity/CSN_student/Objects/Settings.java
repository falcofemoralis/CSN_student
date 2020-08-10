package com.BSLCommunity.CSN_student.Objects;

import android.content.SharedPreferences;

public class Settings {

    // Строковые константы - ключи, по которым хранятся все данные в файле настроек
    public enum PrefKeys {
        USER_ID("user_id"),
        NICKNAME("nickname"),
        PASSWORD("password"),
        GROUP("group"),
        GROUP_ID("group_id"),
        COURSE("course"),
        IS_REGISTERED("is_registered"),
        OFFLINE_DATA("offline_data");

        private String value;
        private PrefKeys(String value) {
            this.value = value;
        }

        public String getKey() {
            return value;
        }
    }

    public static SharedPreferences sharedPrefs;
}
