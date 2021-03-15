package com.BSLCommunity.CSN_student.Models;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Pair;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.BSLCommunity.CSN_student.App;
import com.BSLCommunity.CSN_student.Managers.FileManager;
import com.BSLCommunity.CSN_student.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

public class UserData {
    public static transient UserData instance = null;
    public final String FILE_NAME_SUBJECT_INFO = "subjectsInfo";

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

    public User user;
    public ArrayList<EditableSubject> editableSubjects;

    private UserData() {}
    public static UserData getUserData() {
        if (instance == null) {
            instance = new UserData();
            instance.init();
        }
        return instance;
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
            this.user = new User();
            this.user.setNickName(pref.getString(PrefKeys.NICKNAME.getKey(), null));
            this.user.setPassword(pref.getString(PrefKeys.PASSWORD.getKey(), null));
            this.user.setGroupId(pref.getInt(PrefKeys.GROUP_ID.getKey(), -1));
            this.user.setGroupName(pref.getString(PrefKeys.GROUP.getKey(), null));
            this.user.setCourse(pref.getInt(PrefKeys.COURSE.getKey(), -1));
            this.user.setToken(pref.getString(PrefKeys.TOKEN.getKey(), null));
        }
        catch (Exception ignored) {
            this.user = new User();
        }

        try {
            String jsonSubjectInfo = FileManager.readFile(FILE_NAME_SUBJECT_INFO);
            Type type = new TypeToken<ArrayList<EditableSubject>>() {}.getType();
            this.editableSubjects = (new Gson()).fromJson(jsonSubjectInfo, type);
        }
        catch (Exception ignored) {
            this.editableSubjects = new ArrayList<>();
        }
    }

    /**
     * Обновление данных пользователя (полное обновление)
     * @param newUserData - новые данные
     */
    public void setUser(User newUserData) {
        this.user.setNickName(newUserData.getNickName());
        this.user.setPassword(newUserData.getPassword());
        this.user.setGroupId(newUserData.getGroupId());
        this.user.setGroupName(newUserData.getGroupName());
        this.user.setCourse(newUserData.getCourse());
        this.user.setToken(newUserData.getToken());

        this.saveData();
    }

    /**
     * Обновление данных пользователя (никнейм и пароль)
     * @param nickName - никнейм
     * @param password - пароль
     */
    public void updateUserData(String nickName, String password) {
        this.user.setNickName(nickName);
        this.user.setNickName(password);
        this.saveData();
    }

    /**
     * Установка редактируемых дисциплин
     * @param editableSubjects - редакутируемые дисциплины
     */
    public void setEditableSubjects(ArrayList<EditableSubject> editableSubjects) {
        this.editableSubjects = editableSubjects;
    }

    /**
     * Создание новых редактируемых дисциплин исходя из текущих дисциплин пользователя на курсе
     * @param subjects - текущие дисциплины пользователя, которые необходимо преобразовать
     */
    public void createEditableSubjects(ArrayList<Subject> subjects) {
        for (int i = 0; i < subjects.size(); ++i) {
            this.editableSubjects.add(new EditableSubject(subjects.get(i)));
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
     * Сохранение данных всех пользователя
     */
    public void saveData() {
        // Сохранение данных пользователя и настроек в SharedPreferences
        SharedPreferences.Editor prefEditor = this.encryptedSharedPreferences.edit();
        prefEditor.putString(PrefKeys.NICKNAME.getKey(), this.user.getNickName());
        prefEditor.putString(PrefKeys.PASSWORD.getKey(), this.user.getPassword());
        prefEditor.putString(PrefKeys.GROUP.getKey(), this.user.getGroupName());
        prefEditor.putInt(PrefKeys.GROUP_ID.getKey(), this.user.getGroupId());
        prefEditor.putInt(PrefKeys.COURSE.getKey(), this.user.getCourse());
        prefEditor.putString(PrefKeys.TOKEN.getKey(), this.user.getToken());
        prefEditor.apply();

        if (editableSubjects != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<EditableSubject>>(){}.getType();
            String jsonString = gson.toJson(editableSubjects, type);

            try {
                FileManager.writeFile(FILE_NAME_SUBJECT_INFO, jsonString, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Удаление всех данных на устройстве
     */
    public void clearData() {
        encryptedSharedPreferences.edit().clear().apply();
        FileManager.deleteAllFiles();
    }
}
