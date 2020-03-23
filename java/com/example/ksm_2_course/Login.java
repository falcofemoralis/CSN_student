package com.example.ksm_2_course;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Login extends AppCompatActivity {

    String FILE_NAME = "data_disc_";
    EditText nickNameS;
    EditText passwordS;
    String URL = MainActivity.MAIN_URL + "getUser.php";
    RequestQueue requestQueue;
    String nickname, password, group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        createClickableSpan();
    }

    public void OnClick(View v) {
        nickNameS = (EditText) findViewById(R.id.Nick);
        passwordS = (EditText) findViewById(R.id.pass);
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject user = new JSONObject(response);
                    nickname = user.getString("NickName");
                    FILE_NAME += nickname + ".json";
                    password = user.getString("Password");
                    group = user.getString("NameGroup");
                    if (passwordS.getText().toString().toLowerCase().equals(password)) {
                        Toast.makeText(Login.this, R.string.successfully_login, Toast.LENGTH_SHORT).show();
                        Save();
                    } else {
                        Toast.makeText(Login.this, R.string.inccorect_password, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(Login.this, R.string.no_user, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Login.this, R.string.no_connection, Toast.LENGTH_SHORT).show();
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

    public void OnClickRegistration() {
        Intent intent;
        intent = new Intent(this, Registration.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void Save() {
        SharedPreferences.Editor prefEditor = MainActivity.encryptedSharedPreferences.edit();
        prefEditor.putBoolean(Settings2.KEY_IS_REGISTERED, true);
        prefEditor.putString(Settings2.KEY_NICKNAME, nickname);
        prefEditor.putString(Settings2.KEY_PASSWORD, password);
        prefEditor.putString(Settings2.KEY_GROUP, group);
        prefEditor.apply();

        ArrayList<Discipline> discs = new ArrayList<Discipline>();
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Discipline>>() {
        }.getType();
        discs = gson.fromJson(JSONHelper.read(this, FILE_NAME), listType);

        for (int i = 0; i < discs.size(); ++i) {
            Discipline temp = discs.get(i);
            updateRating(nickname, temp.getName(), gson.toJson(temp.getComplete()));
        }
        Intent intent;
        intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    protected void updateRating(final String NickName, final String NameDiscp, final String status) {
        String url = MainActivity.MAIN_URL + "updateRating.php";

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
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
                parameters.put("NickName", NickName);
                parameters.put("NameDiscp", NameDiscp);
                parameters.put("Status", status);
                return parameters;
            }
        };
        requestQueue.add(request);
    }

    protected void createClickableSpan()
    {
        TextView text = findViewById(R.id.Span_2);

        SpannableString ss = new SpannableString(text.getText());

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                OnClickRegistration();
            }

            @Override
            public void updateDrawState(TextPaint ds)
            {
                super.updateDrawState(ds);
                ds.setColor(0xFF5EE656);
            }
        };
        ss.setSpan(clickableSpan, 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        text.setText(ss);
        text.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
