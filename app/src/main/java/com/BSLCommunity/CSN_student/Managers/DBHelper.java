package com.BSLCommunity.CSN_student.Managers;

import android.content.Context;
import android.util.Log;

import com.BSLCommunity.CSN_student.Activities.MainActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class DBHelper {

    private static String MAIN_URL = "http://a0466974.xsph.ru/"; // Базовый URL сервера
    private static String TAG_LOG_ERROR = "Response error"; // Тег для логов с ошибками с работой сервера
    private static RequestQueue requestQueue = null; // Очередь запросов

    /* Интерфейс колбека для работы с DBHelper
    * Методы:
    * call(String) - метод должен вызываться в случае если запрос был успешно обработан сервером. В метод передается строка ответа с сервера.
    * fail(String) - метод должен вызываться в случае если сервер не смог обработать запрос с сервера. В метод передается строка с сообщением об ошибке
     * */
    public interface CallBack {
        void call(String response);
        void fail(String message);
    }

    //TODO
    // Обдумать как реализовать разные виды запросов

    /* Запросы типа GET
    * Параметры:
    * appContext - контекст приложения
    * apiUrl - вторая часть запроса, соответствующая API
    * callBack - интерфейс колбека класса DBHelper
    * */
    public static void getRequest(Context appContext, String apiUrl, final CallBack callBack) {
        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(appContext);
        String url = MAIN_URL + apiUrl;

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callBack.call(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG_LOG_ERROR, error.toString());
                callBack.fail(error.toString());
            }
        });

        //TODO
        //request.setRetryPolicy(new DefaultRetryPolicy(200,3,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }

    /* Запросы типа POST
     * Параметры:
     * appContext - контекст приложения
     * apiUrl - вторая часть запроса, соответствующая API
     * callBack - интерфейс колбека класса DBHelper
     * params - параметры POST запроса
     * */
    public static void postRequest(Context appContext, String apiUrl, final Map<String, String> params, final CallBack callBack) {
        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(appContext);
        String url = MainActivity.MAIN_URL + apiUrl;

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callBack.call(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG_LOG_ERROR, error.toString());
                callBack.fail(error.toString());
            }
        })  {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };

        requestQueue.add(request);
    }

    /* Запросы типа UPDATE
     * Параметры:
     * appContext - контекст приложения
     * apiUrl - вторая часть запроса, соответствующая API
     * callBack - интерфейс колбека класса DBHelper
     * params - параметры UPDATE запроса
     * */
    public static void putRequest(Context appContext, String apiUrl, final Map<String, String> params, final  CallBack callBack) {
        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(appContext);

        String url = MainActivity.MAIN_URL + apiUrl;
        StringRequest putRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        callBack.call(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG_LOG_ERROR, error.toString());
                        callBack.fail(error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<String, String>();
                // headers.put("Content-Type", "application/json");
                //or try with this:
                headers.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
                return headers;
            }
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };
        requestQueue.add(putRequest);
    }
}
