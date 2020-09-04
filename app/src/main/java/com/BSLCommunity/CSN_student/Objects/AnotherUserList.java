package com.BSLCommunity.CSN_student.Objects;

import android.content.Context;
import android.widget.Toast;

import com.BSLCommunity.CSN_student.Activities.MainActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AnotherUserList {

    public static class AnotherUser {
        public String nickName;

        public AnotherUser(String nickName) {
            this.nickName = nickName;
        }
    }
    public static ArrayList<AnotherUser> users = new ArrayList<>();

    public static void getUsersFromServer(final Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = MainActivity.MAIN_URL + "api/users/course/" + User.getInstance().course;

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);

                    for (int i = 0; i < jsonArray.length(); ++i) {
                        JSONObject anotherUser = jsonArray.getJSONObject(i);
                        users.add(new AnotherUser(anotherUser.getString("NickName")));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT);
            }
        });

        requestQueue.add(request);
    }
}
