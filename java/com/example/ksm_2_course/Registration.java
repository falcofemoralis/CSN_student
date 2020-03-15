package com.example.ksm_2_course;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Registration extends AppCompatActivity {

    final String FILE_NAME = "data_disc.json";
    EditText password, checkPassword, nickName;
    String group;
    Button registration;
    RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        password = (EditText) findViewById(R.id.pass);
        checkPassword = (EditText) findViewById(R.id.checkPass);
        nickName = (EditText) findViewById(R.id.Nick);
        registration = (Button) findViewById(R.id.button2);
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        //выбор группы, в oncreate т.к нужно чтобы по умолчанию попадало что-то в группу
        final Spinner groupSpinner = (Spinner) findViewById(R.id.group);
        groupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                group = groupSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

    }

    public void OnClickLogin(View view){
        Intent intent;
        intent = new Intent(this, Login.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    public void registration(View view) {
        String url = MainActivity.MAIN_URL + "registration.php";
        String name = nickName.getText().toString();
        if (name.equals("")) {
            Toast.makeText(Registration.this, "Please enter nickname", Toast.LENGTH_SHORT).show();
            return;
        }

        String pass = password.getText().toString();
        String checkpass = checkPassword.getText().toString();
        if (!pass.equals(checkpass)) {
            Toast.makeText(Registration.this, "Incorrect password", Toast.LENGTH_SHORT).show();
            return;
        } else if (pass.equals("") || checkpass.equals("")) {
            Toast.makeText(Registration.this, "Enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.indexOf("Duplicate") != -1)
                    Toast.makeText(Registration.this, "This nickname is taken by another user", Toast.LENGTH_SHORT).show();
                else {
                    Toast.makeText(Registration.this, "Successfully registration", Toast.LENGTH_SHORT).show();

                    Gson gson = new Gson();
                    ArrayList<Discipline> discs = new ArrayList<Discipline>();
                    Type listType = new TypeToken<List<Discipline>>() {
                    }.getType();
                    discs = gson.fromJson(JSONHelper.read(Registration.this, FILE_NAME), listType);

                    for (int i = 0; i < discs.size(); ++i)
                    {
                        Discipline temp = discs.get(i);
                        setEmptyRating(temp.getName(), gson.toJson(temp.getComplete()));
                    }

                    Save();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Registration.this, "No connection", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("NickName", nickName.getText().toString().toLowerCase());
                parameters.put("Password", password.getText().toString());
                parameters.put("NameGroup", group);
                return parameters;
            }
        };
        requestQueue.add(request);
    }

    public void Save()
    {
        SharedPreferences.Editor prefEdit = PreferenceManager.getDefaultSharedPreferences(this).edit();
        prefEdit.putBoolean(SettingsActivity.KEY_IS_REGISTERED,false);
        prefEdit.putString(SettingsActivity.KEY_NICKNAME,nickName.getText().toString());
        prefEdit.putString(SettingsActivity.KEY_PASSWORD,password.getText().toString());
        prefEdit.putString(SettingsActivity.KEY_GROUP,group);
        prefEdit.apply();

        Intent intent;
        intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    protected void setEmptyRating(final String NameDiscp, final String status)
    {
        String url = MainActivity.MAIN_URL + "insertRating.php";

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
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
                parameters.put("NameDiscp", NameDiscp);
                parameters.put("Status", status);
                return parameters;
            }
        };
        requestQueue.add(request);
    }

}

