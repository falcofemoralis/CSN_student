package com.BSLCommunity.CSN_student.Models;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.BSLCommunity.CSN_student.APIs.UserApi;
import com.BSLCommunity.CSN_student.Managers.DBHelper;
import com.BSLCommunity.CSN_student.R;
import com.BSLCommunity.CSN_student.lib.CallBack;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.util.Map;
import java.util.concurrent.Callable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

// Singleton класс, паттерн необходимо потому что данные пользователя сериализуются
public class UserModel {

    public static transient UserModel instance = null;
    private transient Retrofit retrofit;

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

    private UserModel() {}
    public static UserModel getUserModel() {
        if (instance == null) {
            instance = new UserModel();
            instance.init();
        }
        return instance;
    }

    //удаление юзера
    public static void deleteUser() {
       instance = null;
    }

    /**
     * Инициализация пользователя (извлекается из зашифрованного локального файла) и объекта ретрофита
     */
    private void init() {
        this.retrofit = new Retrofit.Builder()
                .baseUrl(UserApi.API_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        try {
            SharedPreferences pref = Settings.encryptedSharedPreferences;
            this.nickName =  pref.getString(Settings.PrefKeys.NICKNAME.getKey(), null);
            this.password = pref.getString(Settings.PrefKeys.PASSWORD.getKey(), null);
            this.groupId = pref.getInt(Settings.PrefKeys.GROUP_ID.getKey(), -1);
            this.groupName = pref.getString(Settings.PrefKeys.GROUP.getKey(), null);
            this.course = pref.getInt(Settings.PrefKeys.COURSE.getKey(), -1);
            this.token = pref.getString(Settings.PrefKeys.TOKEN.getKey(), null);
        }
        catch (Exception ignored) {}
    }

    /**
     * Логин пользователя
     * @param nickname - никнейм
     * @param password - пароль
     * @param callBack - колбек, call - не возвращает ничего, fail - возврат ошибки
     */
    public void login(final String nickname, final String password, final CallBack<Void> callBack) {
        UserApi userApi = retrofit.create(UserApi.class);
        Call<UserModel> call = userApi.login(nickname, password);

        call.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(@NotNull Call<UserModel> call, @NotNull Response<UserModel> response) {
                switch (response.code()) {
                    case 200:
                        // Выглядит немного туповато, из-за того что ретрофит не сериализуется и в принципе это нарушает Singleton паттерн
                        UserModel newUserModel = response.body();
                        newUserModel.retrofit = instance.retrofit;
                        instance = newUserModel;
                        instance.saveData();

                        callBack.call(null);
                        break;
                    case 404:
                        callBack.fail(R.string.incorrect_password_or_nickname);
                        break;
                    case 500:
                        callBack.fail(R.string.no_connection_server);
                        break;
                }
            }

            @Override
            public void onFailure(@NotNull Call<UserModel> call, @NotNull Throwable t) {
                callBack.fail(R.string.no_connection_server);
            }
        });

    }

    /**
     * Регистрация нового пользователя, при успешной регистрации устанавливается токен
     * @param nickname - никнейм
     * @param password - пароль
     * @param groupName - название группы
     * @param callBack - колбек, call - не возвращает ничего, fail - возврат ошибки
     */
    public void registration(final String nickname, final String password, final String groupName, final CallBack<Void> callBack) {
        UserApi userApi = retrofit.create(UserApi.class);
        Call<UserModel> call = userApi.registration(nickname, password, groupName);

        call.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(@NotNull Call<UserModel> call, @NotNull Response<UserModel> response) {
                switch (response.code()) {
                    case 200:
                        // Выглядит немного туповато, из-за того что ретрофит не сериализуется и в принципе это нарушает Singleton паттерн
                        UserModel newUserModel = response.body();
                        newUserModel.retrofit = instance.retrofit;
                        instance = newUserModel;
                        instance.saveData();

                        callBack.call(null);
                        break;
                    case 409:
                        callBack.fail(R.string.user_exist);
                        break;
                    case 500:
                        callBack.fail(R.string.no_connection_server);
                        break;
                }
            }

            @Override
            public void onFailure(@NotNull Call<UserModel> call, @NotNull Throwable t) {
                callBack.fail(R.string.no_connection_server);
            }
        });
    }

    /* Функция обновления данных пользователя
     * Параметры:
     * appContext - application context
     * activityContext - activity context активити из которого был сделан вызов функции
     * updateData - параметры которые необходимо передать в PUT запросе при обновления данных пользователя
     * */
    public void update(final Context appContext, final Context activityContext, final Map<String, String> updateData,  final Callable<Void> callBack) throws JSONException {
        // Передается старый пароль, для некого подтверждения пользователя, чтобы никто другой не кидал PUT запросы на сервер и не менял спокойно данные пользователей
        updateData.put("OldPassword", instance.password);
        String apiUrl = String.format("api/users/%1$s", 0);

        DBHelper.putRequest(appContext, apiUrl, updateData, new DBHelper.CallBack<String>() {
            @Override
            public void call(String response) {
                if (response.contains("ERROR"))
                    Toast.makeText(activityContext, R.string.incorrect_data, Toast.LENGTH_SHORT).show();
                else if (response.contains("Duplicate"))
                    Toast.makeText(activityContext, R.string.nickname_is_taken, Toast.LENGTH_SHORT).show();
                else {
                    Toast.makeText(activityContext, R.string.datachanged, Toast.LENGTH_SHORT).show();
                    // Обновление успешно, потому заносим новые данные
                    instance.nickName = updateData.get("NickName");
                    instance.password = updateData.get("Password");
                    saveData(); // сохраняем данные
                    try {
                        callBack.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void fail(String message) {

            }
        });
    }

    // Сохранение всех данных пользователя
    public void saveData() {
        SharedPreferences.Editor prefEditor = Settings.encryptedSharedPreferences.edit();
        prefEditor.putString(Settings.PrefKeys.NICKNAME.getKey(), instance.nickName);
        prefEditor.putString(Settings.PrefKeys.PASSWORD.getKey(), instance.password);
        prefEditor.putString(Settings.PrefKeys.GROUP.getKey(), instance.groupName);
        prefEditor.putInt(Settings.PrefKeys.GROUP_ID.getKey(), instance.groupId);
        prefEditor.putInt(Settings.PrefKeys.COURSE.getKey(), instance.course);
        prefEditor.putString(Settings.PrefKeys.TOKEN.getKey(), instance.token);
        prefEditor.apply();
    }
}
