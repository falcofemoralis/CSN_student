package com.example.ksm_2_course;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class Registration extends AppCompatActivity {

    JSONArray user;
    EditText password, checkPassword, nickName;
    Button registration;
    RequestQueue requestQueue;
    String url = "http://192.168.0.103/registr/InsertNewUser.php",getUserURL = "http://192.168.0.103/registr/getUser.php";
    Setting setting = new Setting();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        password = (EditText) findViewById(R.id.pass);
        checkPassword = (EditText) findViewById(R.id.checkPass);
        nickName = (EditText) findViewById(R.id.Nick);
        registration = (Button) findViewById(R.id.button2);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        Save();

    }

    public void OnClick(View view)
    {

        getUser();
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        String name = nickName.getText().toString();
        if(name.equals("")){
            Toast.makeText(Registration.this, "Please enter nickname", Toast.LENGTH_SHORT).show();
            return;
        }

        String pass = password.getText().toString();
        String checkpass = checkPassword.getText().toString();
       if (!pass.equals(checkpass)){
           Toast.makeText(Registration.this, "Incorrect password", Toast.LENGTH_SHORT).show();
           return;
       }

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("NickName", nickName.getText().toString());
                parameters.put("Password", password.getText().toString());
                return parameters;
            }
        };
        Save();
        requestQueue.add(request);

    }


    public void getUser()
    {

        Map<String, String> params = new HashMap();
        params.put("NickName", nickName.getText().toString());
        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                getUserURL, parameters, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    user = response.getJSONArray("students");

                    if (user.length() != 0)
                        nickName.setText("GG");
                    //Toast.makeText(Registration.this, "This nickname is taken by another user", Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            { }
        }

        );

        requestQueue.add(jsonObjectRequest);
    }

    public void Save(){
        setting.setIsRegistered(1);
        Gson gson = new Gson();
        String jsonString = gson.toJson(setting);
        JSONHelper.create(this, "defaultSettings.json" , jsonString);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
