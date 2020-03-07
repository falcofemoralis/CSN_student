package com.example.ksm_2_course;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class Login extends AppCompatActivity{

    EditText nickNameS;
    EditText passwordS;
    String url = "http://192.168.0.105/registr/Login.php";
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        nickNameS = (EditText) findViewById(R.id.Nick);
        passwordS = (EditText) findViewById(R.id.pass);
        Button login = (Button) findViewById(R.id.button2);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
    }

    public void OnClick(View v){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray students = response.getJSONArray("students");
                    for (int i = 0; i < students.length(); i++) {
                        JSONObject student = students.getJSONObject(i);

                        String nickname = student.getString("NickName");
                        String password = student.getString("Password");

                        if(nickNameS.getText().toString().equals(nickname) && passwordS.getText().toString().equals(password)){
                            Toast.makeText(Login.this, "Successfully login", Toast.LENGTH_SHORT).show();
                            Save();
                            break;
                        }else if(i==students.length()-1){
                            Toast.makeText(Login.this, "Inccorect password or login", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    public void OnClickRegistration(View v){
        Intent intent;
        intent = new Intent(this, Registration.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void Save()
    {
        SharedPreferences.Editor pref = PreferenceManager.getDefaultSharedPreferences(this).edit();
        pref.putBoolean(SettingsActivity.KEY_IS_REGISTERED,false);
        pref.apply();
        Intent intent;
        intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
