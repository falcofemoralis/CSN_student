package com.example.ksm_2_course;

import android.content.Context;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;


public class RequestHelper
{

    public static void request(final Context activity, String URL, final Map<String, String> params, RequestQueue requestQueue)
    {
        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                if (response.indexOf("Duplicate") != -1)
                    Toast.makeText(activity, "This nickname is taken by another user", Toast.LENGTH_SHORT).show();
                else{
                    Toast.makeText(activity, "Successfully registration", Toast.LENGTH_SHORT).show();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity, "No connection", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        requestQueue.add(request);
    }
}
