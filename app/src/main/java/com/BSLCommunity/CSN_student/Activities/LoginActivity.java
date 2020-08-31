package com.BSLCommunity.CSN_student.Activities;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.BSLCommunity.CSN_student.Activities.Schedule.ScheduleActivity;
import com.BSLCommunity.CSN_student.Managers.AnimationManager;
import com.BSLCommunity.CSN_student.Objects.User;
import com.BSLCommunity.CSN_student.R;

// Форма логина для пользователя
public class LoginActivity extends AppCompatActivity{
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnimationManager.setAnimation(getWindow(), this);
        setContentView(R.layout.activity_login);
        createClickableSpan();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Button loginButton = (Button) findViewById(R.id.activity_login_bt_login);
        loginButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                TransitionDrawable transitionDrawable = (TransitionDrawable) view.getBackground();

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    transitionDrawable.startTransition(150);
                    view.startAnimation(AnimationUtils.loadAnimation(LoginActivity.this, R.anim.btn_pressed));
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    EditText NickName = (EditText) findViewById(R.id.activity_login_et_nickname) ;
                    EditText Password = (EditText) findViewById(R.id.activity_login_et_password) ;
                    User.login(getApplicationContext(), LoginActivity.this, NickName.getText().toString().toLowerCase(),Password.getText().toString());

                    transitionDrawable.reverseTransition(150);
                    view.startAnimation(AnimationUtils.loadAnimation(LoginActivity.this, R.anim.btn_unpressed));
                }
                return false;
            }
        });
    }

    //возращает активити в исходное состояние
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    //обработчик перехода на форму регистрации
    public void OnClickRegistration() {
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this);
        startActivity(new Intent(this, RegistrationActivity.class), options.toBundle());
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


