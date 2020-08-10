package com.BSLCommunity.CSN_student.Activities;

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

import com.BSLCommunity.CSN_student.R;
import com.BSLCommunity.CSN_student.Objects.User;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import static com.BSLCommunity.CSN_student.Activities.Settings.encryptedSharedPreferences;

// Форма регистрации пользователя
public class Registration extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    public final static String FILE_NAME = "data_disc_";
    EditText password, checkPassword, nickName;
    String group;
    Button registration;

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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public void OnClickLogin() {
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
    }

    public void Save() {
        SharedPreferences.Editor prefEditor = encryptedSharedPreferences.edit();
        prefEditor.putBoolean(Settings.KEY_IS_REGISTERED, true);
        prefEditor.putString(Settings.KEY_NICKNAME, nickName.getText().toString());
        prefEditor.putString(Settings.KEY_PASSWORD, password.getText().toString());
        prefEditor.putString(Settings.KEY_GROUP, group);
        prefEditor.apply();

        Intent intent;
        intent = new Intent(this, Subjects.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    protected void setEmptyRating(final String NameDiscp, final String status, final byte IDZ) {
        String url = Main.MAIN_URL + "insertRating.php";

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
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
            protected Map<String, String> getParams() {
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

    protected void createSpinner() {
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        group = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    protected void createClickableSpan() {
        TextView text = findViewById(R.id.Span_2a);

        SpannableString ss = new SpannableString(text.getText());

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                OnClickLogin();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(0xFF5EE656);
            }
        };

        ss.setSpan(clickableSpan, 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        text.setText(ss);
        text.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
