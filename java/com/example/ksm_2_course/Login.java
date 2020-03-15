package com.example.ksm_2_course;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
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


public class Login extends AppCompatActivity{

    EditText nickNameS;
    EditText passwordS;
    String URL = MainActivity.MAIN_URL + "getUser.php";
    RequestQueue requestQueue;
    String nickname, password,group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void OnClick(View v) {
        nickNameS = (EditText) findViewById(R.id.Nick);
        passwordS = (EditText) findViewById(R.id.pass);
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest jsonObjectRequest  = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject user = new JSONObject(response);
                    nickname = user.getString("NickName");
                    password = user.getString("Password");
                    group = user.getString("NameGroup");
                    if (passwordS.getText().toString().toLowerCase().equals(password)){
                        Toast.makeText(Login.this, "Successfully login", Toast.LENGTH_SHORT).show();
                        Save();
                    }else{
                        Toast.makeText(Login.this, "Inccorect password", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(Login.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Login.this, "No connection", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("NickName", nickNameS.getText().toString().toLowerCase());
                return parameters;
            }
        };
        requestQueue.add(jsonObjectRequest);

    }
    
    public void OnClickRegistration(View v){
        Intent intent;
        intent = new Intent(this, Registration.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void Save()
    {
        SharedPreferences.Editor prefEdit = PreferenceManager.getDefaultSharedPreferences(this).edit();
        prefEdit.putBoolean(SettingsActivity.KEY_IS_REGISTERED,false);
        prefEdit.putString(SettingsActivity.KEY_NICKNAME,nickname);
        prefEdit.putString(SettingsActivity.KEY_PASSWORD,password);
        prefEdit.putString(SettingsActivity.KEY_GROUP,group);
        prefEdit.apply();

        Intent intent;
        intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
