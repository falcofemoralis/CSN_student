package com.BSLCommunity.CSN_student.Objects;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.BSLCommunity.CSN_student.Activities.Main;
import com.BSLCommunity.CSN_student.Managers.JSONHelper;
import com.BSLCommunity.CSN_student.R;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

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
    public int сourse;

    //список групп по курсу
    public class groups {
        public int Code_Group;
        public String GroupName;
    }

    public static groups[] GROUPS;

    public static User instance = null;

    public static User getInstance() {
        if (instance == null)
            return init();
        return instance;
    }

    /* Инициализация пользователя (извлекается из локального файла)
     * В случае неудачи будет возвращен null как признак того что пользователь не был создан
     */
    private static User init() {

        try {
            // Извлечение локальных данных пользователя
            instance = new User();

            SharedPreferences pref = Settings.sharedPrefs;
            instance.id =  pref.getInt(Settings.PrefKeys.USER_ID.getKey(), -1);
            instance.nickName =  pref.getString(Settings.PrefKeys.NICKNAME.getKey(), null);
            instance.password = pref.getString(Settings.PrefKeys.PASSWORD.getKey(), null);
            instance.groupId = pref.getInt(Settings.PrefKeys.GROUP_ID.getKey(), -1);
            instance.nameGroup = pref.getString(Settings.PrefKeys.GROUP.getKey(), null);
            instance.сourse = pref.getInt(Settings.PrefKeys.COURSE.getKey(), -1);

            // Если хотя бы один из элементов данных отсутствует, можно считать - они повреждены (ошибка записи, пользователь каким то образом стер, просто стерлись данные)
            if (instance.id == -1 || instance.groupId == -1 || instance.сourse == -1 || instance.nickName == null || instance.password == null || instance.nameGroup == null)
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
     * appContext - application conext ????
     * acivityContext - activity context активити из которого был сделан вызов функции
     * loginData - параметры необходимо передать в GET запросе при логине пользователя
     * */
    public static void login(final Context appContext, final Context activityContext, final Map<String, String> loginData) {
        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(appContext);

        String url = Main.MAIN_URL + "api/users/login";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject user = new JSONObject(response);
                    instance = new User();
                    instance.id = user.getInt("id");
                    instance.nickName = user.getString("NickName");
                    instance.password = user.getString("Password");;
                    instance.сourse = user.getInt("Course");
                    instance.nameGroup = user.getString("GroupName");
                    instance.groupId = user.getInt("group_id");
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
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return  loginData;
            }
        };
        requestQueue.add(request);
    }

    /* Функция регистрации нового пользователя
     * Параметры:
     * appContext - application conext ????
     * acivityContext - activity context активити из которого был сделан вызов функции
     * regData - параметры необходимо передать в GET запросе при регистрации пользователя
     * */
    public static void registration(final Context appContext, final Context activityContext, final Map<String, String> regData) {

        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(appContext);

        String url = Main.MAIN_URL + "/api/users";

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
                    Map<String, String> param = new HashMap<>();
                    param.put("NickName", regData.get("NickName"));
                    param.put("Password", regData.get("Password"));
                    login(appContext, activityContext, param); // Логин загружает все необходимые данные после успешной регистрации пользователя
                    activityContext.startActivity(new Intent(appContext, Main.class));
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
                return  regData;
            }
        };
        requestQueue.add(request);
    }

    /* Функция регистрации нового пользователя
     * Параметры:
     * appContext - application conext ????
     * acivityContext - activity context активити из которого был сделан вызов функции
     * regData - параметры которые необходимо передать в PUT запросе при обновления данных пользователя
     * */
    public  void update(final Context appContext, final Context activityContext, final Map<String, String> updateData) {
        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(appContext);

        String url = Main.MAIN_URL + "/api/users/" + Integer.toString(instance.id);

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
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
        SharedPreferences.Editor prefEditor = Settings.sharedPrefs.edit();
        prefEditor.putInt(Settings.PrefKeys.USER_ID.getKey(), instance.id);
        prefEditor.putString(Settings.PrefKeys.NICKNAME.getKey(), instance.nickName);
        prefEditor.putString(Settings.PrefKeys.PASSWORD.getKey(), instance.password);
        prefEditor.putString(Settings.PrefKeys.GROUP.getKey(), instance.nameGroup);
        prefEditor.putInt(Settings.PrefKeys.GROUP_ID.getKey(), instance.groupId);
        prefEditor.putInt(Settings.PrefKeys.COURSE.getKey(), instance.сourse);
    }

    //получение групп по курсу
    public static void getGroups(final Context context) {
        String url = Main.MAIN_URL + "getGroups.php";
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONHelper.create(context, "groups", response);
                Gson gson = new Gson();
                GROUPS = gson.fromJson(response, groups[].class);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "No connection with our server,try later...", Toast.LENGTH_SHORT).show();
                String response = JSONHelper.read(context, "groups");
                Gson gson = new Gson();
                GROUPS = gson.fromJson(response, groups[].class);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("Course", String.valueOf(3));
                return parameters;
            }
        };
        requestQueue.add(request);
    }
}