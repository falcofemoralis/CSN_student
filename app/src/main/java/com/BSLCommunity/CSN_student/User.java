package com.BSLCommunity.CSN_student;

import android.content.Context;
import android.widget.Toast;

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
    public String nameGroup;
    public int сourse;

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
            // Здесь должен быть код для извлечения данных пользователя из локального хранилища
            return new User();
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

        String url = MainActivity.MAIN_URL + "api/users";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // На данный момент сообщением об неуспешной регистрации является ERROR
                if (response == "ERROR")
                    Toast.makeText(activityContext, "incorrect data", Toast.LENGTH_SHORT).show();
                else {
                    Toast.makeText(activityContext, R.string.successfully_registration, Toast.LENGTH_SHORT).show();

                    /* Для загрузки данных используется логин, потому необходимо передать в функцию необходимые данные для успешного логина
                    * Вызывает логин по причине того что для полных данных о пользователе нужно так же знать его id, который создается в базе автоматически при регистрации
                    * */
                    Map<String, String> param = new HashMap<>();
                    param.put("NickName", regData.get("NickName"));
                    param.put("Password", regData.get("Password"));
                    login(appContext, activityContext, param); // Логин загружает все необходимые данные после успешной регистрации пользователя
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
}
