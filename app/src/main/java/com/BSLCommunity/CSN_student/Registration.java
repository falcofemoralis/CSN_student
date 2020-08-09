package com.BSLCommunity.CSN_student;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

// Форма регистрации пользователя
public class Registration extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String FILE_NAME = "data_disc_";
    EditText password, checkPassword, nickName;
    String group;
    Button registration;
    RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        createSpinner();
        createClickableSpan();

        password = (EditText) findViewById(R.id.pass);
        checkPassword = (EditText) findViewById(R.id.checkPass);
        nickName = (EditText) findViewById(R.id.Nick);
        registration = (Button) findViewById(R.id.button2);
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public void OnClickLogin(){
        Intent intent;
        intent = new Intent(this, Login.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    public void registration(View view) {
        Map<String, String> param = new HashMap<>();

        param.put("NickName", "Arthur");
        param.put("Password", "Farmer Arthur");
        param.put("Group", "КНТ-518");

        User.registration(getApplicationContext(), Registration.this, param);

       /* String url = MainActivity.MAIN_URL + "registration.php";
        String name = nickName.getText().toString();
        FILE_NAME += name + ".json";
        if (name.equals("")) {
            Toast.makeText(Registration.this, R.string.nickname_error, Toast.LENGTH_SHORT).show();
            return;
        }

        String pass = password.getText().toString();
        String checkpass = checkPassword.getText().toString();
        if (!pass.equals(checkpass)) {
            Toast.makeText(Registration.this, R.string.inccorect_password, Toast.LENGTH_SHORT).show();
            return;
        } else if (pass.equals("") || checkpass.equals("")) {
            Toast.makeText(Registration.this, R.string.password_error, Toast.LENGTH_SHORT).show();
            return;
        }

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.indexOf("Duplicate") != -1)
                    Toast.makeText(Registration.this, R.string.nickname_is_taken, Toast.LENGTH_SHORT).show();
                else {
                    Toast.makeText(Registration.this, R.string.successfully_registration, Toast.LENGTH_SHORT).show();

                    Gson gson = new Gson();
                    ArrayList<Discipline> discs = new ArrayList<Discipline>();
                    Type listType = new TypeToken<List<Discipline>>() {
                    }.getType();
                    discs = gson.fromJson(JSONHelper.read(Registration.this, FILE_NAME), listType);

                    for (int i = 0; i < discs.size(); ++i)
                    {
                        Discipline temp = discs.get(i);
                        setEmptyRating(temp.getName(), gson.toJson(temp.getComplete()), temp.getIDZ());
                    }

                    Save();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Registration.this, R.string.no_connection, Toast.LENGTH_SHORT).show();
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
        requestQueue.add(request);*/
    }

    public void Save()
    {
        SharedPreferences.Editor prefEditor = MainActivity.encryptedSharedPreferences.edit();
        prefEditor.putBoolean(Settings2.KEY_IS_REGISTERED,true);
        prefEditor.putString(Settings2.KEY_NICKNAME,nickName.getText().toString());
        prefEditor.putString(Settings2.KEY_PASSWORD,password.getText().toString());
        prefEditor.putString(Settings2.KEY_GROUP,group);
        prefEditor.apply();

        Intent intent;
        intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    protected void setEmptyRating(final String NameDiscp, final String status, final byte IDZ)
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
            protected Map<String, String> getParams(){
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("NickName", nickName.getText().toString().toLowerCase());
                parameters.put("NameDiscp", NameDiscp);
                parameters.put("Status", status);
                parameters.put("IDZ", Byte.toString(IDZ));
                return parameters;
            }
        };
        requestQueue.add(request);
    }

    protected  void createSpinner()
    {
        Spinner coloredSpinner = findViewById(R.id.group);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(
                this,
                R.array.group_values,
                R.layout.color_spinner_layout
        );

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        coloredSpinner.setAdapter(adapter);
        coloredSpinner.setOnItemSelectedListener(this);
    }

    /*protected  void createSpinner()
    {
        Spinner gr_spin = findViewById(R.id.group);

        ArrayList<String> spinnerArray = new ArrayList<String>();

        for (int i = 0; i < MainActivity.GROUPS.length; ++i)
            spinnerArray.add(MainActivity.GROUPS[i].NameGroup);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, R.layout.color_spinner_layout,spinnerArray);

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        gr_spin.setAdapter(adapter);
        gr_spin.setOnItemSelectedListener(this);
    }*/

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
         group = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    protected void createClickableSpan()
    {
        TextView text = findViewById(R.id.Span_2a);

        SpannableString ss = new SpannableString(text.getText());

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                OnClickLogin();
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
