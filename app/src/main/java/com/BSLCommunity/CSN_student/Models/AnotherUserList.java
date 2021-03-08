package com.BSLCommunity.CSN_student.Models;

import android.content.Context;

import java.util.ArrayList;

public class AnotherUserList {

    public static class AnotherUser {
        public String nickName, realName, groupName;

        public AnotherUser(String nickName, String realName, String groupName) {
            this.nickName = nickName;
            this.realName = realName;
            this.groupName = groupName;
        }
    }
    public static ArrayList<AnotherUser> users = new ArrayList<>();

    public static void getUsersFromServer(final Context context) {
//        RequestQueue requestQueue = Volley.newRequestQueue(context);
//        //String url = DBHelper.MAIN_URL + "api/users/course/" + UserModel.getUserModel().course;
//
//        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                try {
//                    JSONArray jsonArray = new JSONArray(response);
//
//                    for (int i = 0; i < jsonArray.length(); ++i) {
//                        JSONObject anotherUser = jsonArray.getJSONObject(i);
//                        String nickName = anotherUser.getString("NickName");
//                        String realName = anotherUser.getString("RealName");
//                        String groupName = anotherUser.getString("GroupName");
//                        users.add(new AnotherUser(nickName, realName, groupName));
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT);
//            }
//        });
//
//        requestQueue.add(request);
    }
}
