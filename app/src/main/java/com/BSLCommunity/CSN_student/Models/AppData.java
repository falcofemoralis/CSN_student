package com.BSLCommunity.CSN_student.Models;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Pair;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.BSLCommunity.CSN_student.App;
import com.BSLCommunity.CSN_student.Managers.FileManager;
import com.BSLCommunity.CSN_student.R;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

public class AppData {
    public static transient AppData instance = null;

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

    public User userData;

    private AppData() {}
    public static AppData getAppData() {
        if (instance == null) {
            instance = new AppData();
            instance.init();
        }
        return instance;
    }

    /**
     * Обновление данных пользователя (полное обновление)
     * @param newUserData - новые данные
     */
    public void updateUserData(User newUserData) {
        this.userData.setNickName(newUserData.getNickName());
        this.userData.setPassword(newUserData.getPassword());
        this.userData.setGroupId(newUserData.getGroupId());
        this.userData.setGroupName(newUserData.getGroupName());
        this.userData.setCourse(newUserData.getCourse());
        this.userData.setToken(newUserData.getToken());

        this.saveData();
    }

    /**
     * Обновление данных пользователя (никнейм и пароль)
     * @param nickName - никнейм
     * @param password - пароль
     */
    public void updateUserData(String nickName, String password) {
        this.userData.setNickName(nickName);
        this.userData.setNickName(password);
        this.saveData();
    }

    /**
     * Интцтадизация данных пользователя. Попытка достать их из зашифрованных SharedPreferences
     */
    private void init() {
        setSettingsFile(App.getApp().getApplicationContext());

        // Добавление языков
        String[] languagesArray = App.getApp().getApplicationContext().getResources().getStringArray(R.array.languages);
        languages.add(new Pair<>(languagesArray[0],"en"));
        languages.add(new Pair<>(languagesArray[1],"ru"));
        languages.add(new Pair<>(languagesArray[2],"uk"));

        try {
            SharedPreferences pref = this.encryptedSharedPreferences;
            this.userData = new User();
            this.userData.setNickName(pref.getString(PrefKeys.NICKNAME.getKey(), null));
            this.userData.setPassword(pref.getString(PrefKeys.PASSWORD.getKey(), null));
            this.userData.setGroupId(pref.getInt(PrefKeys.GROUP_ID.getKey(), -1));
            this.userData.setGroupName(pref.getString(PrefKeys.GROUP.getKey(), null));
            this.userData.setCourse(pref.getInt(PrefKeys.COURSE.getKey(), -1));
            this.userData.setToken(pref.getString(PrefKeys.TOKEN.getKey(), null));
        }
        catch (Exception ignored) {
            this.userData = new User();
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
     * Сохранение данных пользователя в SharedPreferences
     */
    public void saveData() {
        SharedPreferences.Editor prefEditor = this.encryptedSharedPreferences.edit();
        prefEditor.putString(PrefKeys.NICKNAME.getKey(), this.userData.getNickName());
        prefEditor.putString(PrefKeys.PASSWORD.getKey(), this.userData.getPassword());
        prefEditor.putString(PrefKeys.GROUP.getKey(), this.userData.getGroupName());
        prefEditor.putInt(PrefKeys.GROUP_ID.getKey(), this.userData.getGroupId());
        prefEditor.putInt(PrefKeys.COURSE.getKey(), this.userData.getCourse());
        prefEditor.putString(PrefKeys.TOKEN.getKey(), this.userData.getToken());
        prefEditor.apply();
    }

    /**
     * Удаление всех данных на устройстве
     */
    public void clearData() {
        encryptedSharedPreferences.edit().clear().apply();
        FileManager.deleteAllFiles();
    }
}
