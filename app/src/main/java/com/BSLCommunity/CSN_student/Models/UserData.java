package com.BSLCommunity.CSN_student.Models;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Pair;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.BSLCommunity.CSN_student.App;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

public class UserData {
    public static transient UserData instance = null;

    /**
     * PrefKeys - Строковые константы - ключи, по которым хранятся все данные в файле настроек
     * languages - название языков, где пара <название языка, его код>;
     * encryptedSharedPreferences - файл пользовательских настроек и данных
     */
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
    public transient ArrayList<Pair<String, String>> languages = new ArrayList<>(); //
    public transient SharedPreferences encryptedSharedPreferences;

    @SerializedName("NickName")
    public String nickName;
    @SerializedName("Password")
    public String password;
    @SerializedName("group_id")
    public int groupId;
    @SerializedName("GroupName")
    public String groupName;
    @SerializedName("Course")
    public int course;
    @SerializedName("token")
    private String token;

    private UserData() {}
    public static UserData getUserData() {
        if (instance == null) {
            instance = new UserData();
            instance.init();
        }
        return instance;
    }

    public void updateUserData(UserData newUserData) {
        this.nickName = newUserData.nickName;
        this.password = newUserData.password;
        this.groupId = newUserData.groupId;
        this.groupName = newUserData.groupName;
        this.course = newUserData.course;
        this.token = newUserData.token;

        this.saveData();
    }

    /**
     * Интцтадизация данных пользователя. Попытка достать их из зашифрованных SharedPreferences
     */
    private void init() {
        setSettingsFile(App.getApp().getApplicationContext());

        try {
            SharedPreferences pref = this.encryptedSharedPreferences;
            this.nickName =  pref.getString(Settings.PrefKeys.NICKNAME.getKey(), null);
            this.password = pref.getString(Settings.PrefKeys.PASSWORD.getKey(), null);
            this.groupId = pref.getInt(Settings.PrefKeys.GROUP_ID.getKey(), -1);
            this.groupName = pref.getString(Settings.PrefKeys.GROUP.getKey(), null);
            this.course = pref.getInt(Settings.PrefKeys.COURSE.getKey(), -1);
            this.token = pref.getString(Settings.PrefKeys.TOKEN.getKey(), null);
        }
        catch (Exception ignored) {
            this.nickName = null;
            this.password = null;
            this.groupId = -1;
            this.groupName = null;
            this.course = -1;
            this.token = null;
        }
    }

    /**
     * Инициализация SharedPreferences
     * @param context - контекст приложения
     */
    private void setSettingsFile(Context context) {
        MasterKey masterKey = null;
        try {
            masterKey = new MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            this.encryptedSharedPreferences = EncryptedSharedPreferences.create(context,"settings_data",masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Получение токена пользователя (нужен для проверки авторизации и выполнения некоторых запросов)
     * @return токен пользователя
     */
    public String getToken() {
        return token;
    }

    /**
     * Сохранение данных пользователя в SharedPreferences
     */
    public void saveData() {
        SharedPreferences.Editor prefEditor = this.encryptedSharedPreferences.edit();
        prefEditor.putString(PrefKeys.NICKNAME.getKey(), this.nickName);
        prefEditor.putString(PrefKeys.PASSWORD.getKey(), this.password);
        prefEditor.putString(PrefKeys.GROUP.getKey(), this.groupName);
        prefEditor.putInt(PrefKeys.GROUP_ID.getKey(), this.groupId);
        prefEditor.putInt(PrefKeys.COURSE.getKey(), this.course);
        prefEditor.putString(PrefKeys.TOKEN.getKey(), this.token);
        prefEditor.apply();
    }
}
