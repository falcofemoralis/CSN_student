package com.example.ksm_2_course;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import android.content.Intent;
import android.content.SharedPreferences;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;


public class Registration extends AppCompatActivity {

    EditText password, checkPassword, nickName;
    Button registration;
    RequestQueue requestQueue;
    String url = "http://192.168.0.105/registr/InsertNewUser.php",getUserURL = "http://192.168.0.105/registr/getUser.php";
    static boolean isTrue;

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

        String name = nickName.getText().toString();
        if(name.equals("")){
            Toast.makeText(Registration.this, "Please enter nickname", Toast.LENGTH_SHORT).show();
            return;
        }

        getUser();
        if(isTrue) return;

        String pass = password.getText().toString();
        String checkpass = checkPassword.getText().toString();
       if (!pass.equals(checkpass)){
           Toast.makeText(Registration.this, "Incorrect password", Toast.LENGTH_SHORT).show();
           return;
       }else if(pass.equals("") || checkpass.equals("")) {
           Toast.makeText(Registration.this, "Enter password", Toast.LENGTH_SHORT).show();
           return;
       }

        requestQueue = Volley.newRequestQueue(getApplicationContext());
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
        requestQueue.add(request);
        Save();
        Intent intent;
        intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    public void getUser() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("NickName", nickName.getText().toString() );

        JSONObject parameters = new JSONObject(map);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, getUserURL, parameters, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray user = response.getJSONArray("students");
                    JSONObject student = user.getJSONObject(0);
                    String nm = student.getString("NickName");

                    if (nm.equals(nickName.getText().toString())){
                        Toast.makeText(Registration.this, "This nickname is taken by another user", Toast.LENGTH_SHORT).show();
                        isTrue=true;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    isTrue=false;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue.add(jsonObjectRequest);

    }

    public void Save(){
        SharedPreferences.Editor pref = PreferenceManager.getDefaultSharedPreferences(this).edit();
        pref.putBoolean(SettingsActivity.KEY_IS_REGISTERED,false);
        pref.apply();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
