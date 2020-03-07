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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.HashMap;
import java.util.Map;

public class Registration extends AppCompatActivity {

    EditText password, checkPassword, nickName;
    Button registration;
    RequestQueue requestQueue;
    String url = "http://192.168.0.105/registr/InsertNewUser.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        password = (EditText) findViewById(R.id.pass);
        checkPassword = (EditText) findViewById(R.id.checkPass);
        nickName = (EditText) findViewById(R.id.Nick);
        registration = (Button) findViewById(R.id.button2);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
    }

    public void OnClickLogin(View view){
        Intent intent;
        intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    public void OnClick(View view)
    {
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
        }else if(pass.equals("") || checkpass.equals("")) {
            Toast.makeText(Registration.this, "Enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                if (response.indexOf("Duplicate") != -1)
                    Toast.makeText(Registration.this, "This nickname is taken by another user", Toast.LENGTH_SHORT).show();
                else
                    Save();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("NickName", nickName.getText().toString().toLowerCase());
                parameters.put("Password", password.getText().toString());
                return parameters;
            }
        };
        requestQueue.add(request);

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

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

}
