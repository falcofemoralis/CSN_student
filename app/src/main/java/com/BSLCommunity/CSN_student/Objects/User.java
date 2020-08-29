package com.BSLCommunity.CSN_student.Objects;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.BSLCommunity.CSN_student.Activities.MainActivity;
import com.BSLCommunity.CSN_student.R;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


// Singleton класс, паттерн необходимо потому что данные пользователя сериализуются
public class User {

    /* Основные данные пользователя
     * id -  id пользователя
     * nickName - никнейм
     * password - пароль
     * nameGroup - название группы
     * сourse - номер курса
     * */
    public int id;
    public String nickName;
    public String password;
    public int groupId;
    public String nameGroup;
    public int course;

    //реализация синглтона
    public static User instance = null;
    public static User getInstance() {
        if (instance == null)
            return init();
        return instance;
    }

    //удаление юзера
    public static void deleteUser() {
       instance = null;
    }

    /* Инициализация пользователя (извлекается из локального файла)
     * В случае неудачи будет возвращен null как признак того что пользователь не был создан
     */
    private static User init() {

        try {
            // Извлечение локальных данных пользователя
            instance = new User();

            SharedPreferences pref = Settings.encryptedSharedPreferences;
            instance.id =  pref.getInt(Settings.PrefKeys.USER_ID.getKey(), -1);
            instance.nickName =  pref.getString(Settings.PrefKeys.NICKNAME.getKey(), null);
            instance.password = pref.getString(Settings.PrefKeys.PASSWORD.getKey(), null);
            instance.groupId = pref.getInt(Settings.PrefKeys.GROUP_ID.getKey(), -1);
            instance.nameGroup = pref.getString(Settings.PrefKeys.GROUP.getKey(), null);
            instance.course = pref.getInt(Settings.PrefKeys.COURSE.getKey(), -1);

            // Если хотя бы один из элементов данных отсутствует, можно считать - они повреждены (ошибка записи, пользователь каким то образом стер, просто стерлись данные)
            if (instance.id == -1 || instance.groupId == -1 || instance.course == -1 || instance.nickName == null || instance.password == null || instance.nameGroup == null)
                return null;

            return instance;
        }
        catch (Exception ex) {
            // В случае неудачи, если данные к примеру повреждены или их просто нету - возвращает null
            return null;
        }
    }

    /* Функция логина пользователя
     * Параметры:
     * appContext - application context
     * activityContext - activity context активити из которого был сделан вызов функции
     * loginData - параметры необходимо передать в GET запросе при логине пользователя
     * */
    public static void login(final Context appContext, final Context activityContext, final String nickName, final String password) {
        RequestQueue requestQueue = Volley.newRequestQueue(appContext);
        String url = MainActivity.MAIN_URL + String.format("api/users/login?NickName=%1$s&Password=%2$s", nickName,password);

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // Получаем все необходимые данные пользователя
                    JSONObject user = new JSONObject(response);
                    instance = new User();
                    instance.id = user.getInt("id");
                    instance.nickName = user.getString("NickName");
                    instance.password = user.getString("Password");;
                    instance.course = user.getInt("Course");
                    instance.nameGroup = user.getString("GroupName");
                    instance.groupId = user.getInt("group_id");
                    instance.saveData(); // Сохраняем данные

                    //скачиваем группы
                    Groups.init(appContext, instance.course);

                    //запоминаем что пользователь зарегистрировался
                    SharedPreferences.Editor prefEditor = Settings.encryptedSharedPreferences.edit();
                    prefEditor.putBoolean(Settings.PrefKeys.IS_REGISTERED.getKey(), true).apply();

                    //скачиваем рейтинг юзера
                    SubjectsInfo.downloadRating(appContext);

                    //запускаем главное окно
                    activityContext.startActivity(new Intent(appContext, MainActivity.class));
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(appContext, R.string.no_user, Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activityContext, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(request);
    }

    /* Функция регистрации нового пользователя
     * Параметры:
     * appContext - application context
     * activityContext - activity context активити из которого был сделан вызов функции
     * regData - параметры необходимо передать в GET запросе при регистрации пользователя
     * */
    public static void registration(final Context appContext, final Context activityContext, final String nickName, final String password, final String codeGroup) {
        RequestQueue requestQueue = Volley.newRequestQueue(appContext);

        String url = MainActivity.MAIN_URL + "api/users";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // На данный момент сообщением об неуспешной регистрации является ERROR
                if (response.equals("ERROR"))
                    Toast.makeText(activityContext, "incorrect data", Toast.LENGTH_SHORT).show();
                else if (response.equals("Duplicate"))
                    Toast.makeText(activityContext, "user with this nickName already exist", Toast.LENGTH_SHORT).show();
                else {
                    Toast.makeText(activityContext, R.string.successfully_registration, Toast.LENGTH_SHORT).show();

                    /* Для загрузки данных используется логин, потому необходимо передать в функцию необходимые данные для успешного логина
                     * Вызывает логин по причине того что для полных данных о пользователе нужно так же знать его id, который создается в базе автоматически при регистрации
                     * */
                    login(appContext, activityContext, nickName, password); // Логин загружает все необходимые данные после успешной регистрации пользователя
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activityContext, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("NickName", nickName);
                param.put("Password", password);
                param.put("CodeGroup", codeGroup);
                return param;
            }
        };
        requestQueue.add(request);
    }

    /* Функция регистрации нового пользователя
     * Параметры:
     * appContext - application context
     * activityContext - activity context активити из которого был сделан вызов функции
     * regData - параметры которые необходимо передать в PUT запросе при обновления данных пользователя
     * */
    public void update(final Context appContext, final Context activityContext, final Map<String, String> updateData) {
        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(appContext);

        String url = MainActivity.MAIN_URL + "/api/users/1";

        StringRequest request = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Обновление успешно, потому заносим новые данные
                instance.nickName = updateData.get("NickName");
                instance.password = updateData.get("Password");
                saveData(); // сохраняем данные
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activityContext, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Передается старый пароль, для некого подтверждения пользователя, чтобы никто другой не кидал PUT запросы на сервер и не менял спокойно данные пользователей
                updateData.put("OldPassword", instance.password);
                return  updateData;
            }
        };
        requestQueue.add(request);
    }

    // Сохранение всех данных пользователя
    public void saveData()
    {
        SharedPreferences.Editor prefEditor = Settings.encryptedSharedPreferences.edit();
        prefEditor.putInt(Settings.PrefKeys.USER_ID.getKey(), instance.id);
        prefEditor.putString(Settings.PrefKeys.NICKNAME.getKey(), instance.nickName);
        prefEditor.putString(Settings.PrefKeys.PASSWORD.getKey(), instance.password);
        prefEditor.putString(Settings.PrefKeys.GROUP.getKey(), instance.nameGroup);
        prefEditor.putInt(Settings.PrefKeys.GROUP_ID.getKey(), instance.groupId);
        prefEditor.putInt(Settings.PrefKeys.COURSE.getKey(), instance.course);
        prefEditor.apply();
    }
}
