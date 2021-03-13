package com.BSLCommunity.CSN_student.Models;

import android.content.Context;

import com.BSLCommunity.CSN_student.APIs.UserApi;
import com.BSLCommunity.CSN_student.R;
import com.BSLCommunity.CSN_student.lib.ExCallable;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// Singleton класс, паттерн необходимо потому что данные пользователя сериализуются
public class UserModel {

    public static UserModel instance = null;
    private Retrofit retrofit;

    private UserModel() {}
    public static UserModel getUserModel() {
        if (instance == null) {
            instance = new UserModel();
            instance.init();
        }
        return instance;
    }

    /**
     * Инициализация ретрофита
     */
    private void init() {
        this.retrofit = new Retrofit.Builder()
                .baseUrl(UserApi.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }

    /**
     * Логин пользователя
     * @param nickname - никнейм
     * @param password - пароль
     * @param exCallable - колбек, call - не возвращает ничего, fail - возврат ошибки
     */
    public void login(final String nickname, final String password, final ExCallable<User> exCallable) {
        UserApi userApi = retrofit.create(UserApi.class);
        Call<User> call = userApi.login(nickname, password);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NotNull Call<User> call, @NotNull Response<User> response) {
                switch (response.code()) {
                    case 200:
                        exCallable.call(response.body());
                        break;
                    case 404:
                        exCallable.fail(R.string.incorrect_password_or_nickname);
                        break;
                    case 500:
                        exCallable.fail(R.string.no_connection_server);
                        break;
                }
            }

            @Override
            public void onFailure(@NotNull Call<User> call, @NotNull Throwable t) {
                exCallable.fail(R.string.no_connection_server);
            }
        });

    }

    /**
     * Регистрация нового пользователя, при успешной регистрации устанавливается токен
     * @param nickname - никнейм
     * @param password - пароль
     * @param groupName - название группы
     * @param exCallable - колбек, call - не возвращает ничего, fail - возврат ошибки
     */
    public void registration(final String nickname, final String password, final String groupName, final ExCallable<User> exCallable) {
        UserApi userApi = retrofit.create(UserApi.class);
        Call<User> call = userApi.registration(nickname, password, groupName);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NotNull Call<User> call, @NotNull Response<User> response) {
                switch (response.code()) {
                    case 200:
                        exCallable.call(response.body());
                        break;
                    case 409:
                        exCallable.fail(R.string.user_exist);
                        break;
                    case 500:
                        exCallable.fail(R.string.no_connection_server);
                        break;
                }
            }

            @Override
            public void onFailure(@NotNull Call<User> call, @NotNull Throwable t) {
                exCallable.fail(R.string.no_connection_server);
            }
        });
    }

    /**
     * Обновление данных пользователя
     * @param nickName - новый никнейм
     * @param password - новый пароль
     * @param exCallable - колбек
     */
    public void update(String nickName, String password, String token, final ExCallable<Void> exCallable) {
        UserApi userApi = retrofit.create(UserApi.class);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("NickName", nickName);
        jsonObject.addProperty("Password", password);

        Call<Void> call = userApi.updateUserData(token, jsonObject);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NotNull Call<Void> call, @NotNull Response<Void> response) {
                switch (response.code()) {
                    case 200:
                        exCallable.call(null);
                        break;
                    case 401:
                        exCallable.fail(R.string.no_auth);
                        break;
                }
            }

            @Override
            public void onFailure(@NotNull Call<Void> call, @NotNull Throwable t) {
                exCallable.fail(R.string.no_connection_server);
            }
        });

    }

    //TODO
    public static void updateRating(final Context context, final String JSONString) throws JSONException {

    }

    //TODO
    public static void downloadRating(final Context context) throws JSONException {

    }
}
