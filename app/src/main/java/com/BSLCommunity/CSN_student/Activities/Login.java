package com.BSLCommunity.CSN_student.Activities;

import android.content.Intent;
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

import com.BSLCommunity.CSN_student.Objects.User;
import com.BSLCommunity.CSN_student.R;

// Форма логина для пользователя
public class Login extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        createClickableSpan();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        findViewById(R.id.activity_login_bt_login).setClickable(true);
    }

    //возращает активити в исходное состояние
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    //кнопка логина
    public void OnClick(View v) {
        v.setClickable(false);
        EditText NickName = (EditText) findViewById(R.id.activity_login_et_nickname) ;
        EditText Password = (EditText) findViewById(R.id.activity_login_et_password) ;
        User.login(getApplicationContext(), Login.this, NickName.getText().toString().toLowerCase(),Password.getText().toString());
    }

    //обработчик перехода на форму регистрации
    public void OnClickRegistration() {
        startActivity(new Intent(this, Registration.class));
        overridePendingTransition(0, 0);
    }

    //кнопка перехода в регистрацию
    protected void createClickableSpan() {
        TextView text = findViewById(R.id.activity_login_tv_span2);

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


