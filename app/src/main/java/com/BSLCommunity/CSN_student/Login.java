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
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;

import java.util.HashMap;
import java.util.Map;

import static com.BSLCommunity.CSN_student.MainActivity.encryptedSharedPreferences;

// Форма логина для пользователя
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

    // Логин
    public void OnClick(View v) {
        Map<String, String> param = new HashMap<>();

        param.put("NickName", "Arthur");
        param.put("Password", "Farmer Arthur");

        User.registration(getApplicationContext(), Login.this, param);
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
        SharedPreferences.Editor prefEditor = encryptedSharedPreferences.edit();
        prefEditor.putBoolean(Settings2.KEY_IS_REGISTERED, true);
        prefEditor.putString(Settings2.KEY_NICKNAME, nickname);
        prefEditor.putString(Settings2.KEY_PASSWORD, password);
        prefEditor.putString(Settings2.KEY_GROUP, group);
        prefEditor.apply();

        Intent intent;
        intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    protected void createClickableSpan() {
        TextView text = findViewById(R.id.Span_2);

        SpannableString ss = new SpannableString(text.getText());

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                OnClickRegistration();
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


