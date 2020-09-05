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
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.BSLCommunity.CSN_student.Managers.AnimationManager;
import com.BSLCommunity.CSN_student.Objects.Groups;
import com.BSLCommunity.CSN_student.Objects.User;
import com.BSLCommunity.CSN_student.R;
import com.github.ybq.android.spinkit.style.ThreeBounce;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

// Форма регистрации пользователя
public class RegistrationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    long id; //выбранный код группы со спиннера
    ProgressBar progressBar; //анимация загрузки в спиннере групп

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnimationManager.setAnimation(getWindow(), this);
        setContentView(R.layout.activity_registration);
        progressBar = (ProgressBar) findViewById(R.id.activity_registration_pb_groups);
        progressBar.setIndeterminateDrawable(new ThreeBounce());
        createCourseSpinner();
        createClickableSpan();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Button registrationButton = (Button) findViewById(R.id.activity_registration_bt_register);
        registrationButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                TransitionDrawable transitionDrawable = (TransitionDrawable) view.getBackground();

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    transitionDrawable.startTransition(150);
                    view.startAnimation(AnimationUtils.loadAnimation(RegistrationActivity.this, R.anim.btn_pressed));
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    EditText NickName = (EditText) findViewById(R.id.activity_registration_et_nickname);
                    EditText Password = (EditText) findViewById(R.id.activity_registration_et_password);
                    EditText RepeatPassword = (EditText) findViewById(R.id.activity_registration_et_passwordRe);

                    if (Password.getText().toString().equals(RepeatPassword.getText().toString())) {
                        User.registration(getApplicationContext(), RegistrationActivity.this, NickName.getText().toString().toLowerCase(), Password.getText().toString(), Integer.toString((Groups.groupsLists.get((int) id).id)));
                    } else {
                        Toast.makeText(RegistrationActivity.this, R.string.inccorect_password, Toast.LENGTH_SHORT).show();
                    }
                    transitionDrawable.reverseTransition(150);
                    view.startAnimation(AnimationUtils.loadAnimation(RegistrationActivity.this, R.anim.btn_unpressed));
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

    //создание спиннера групп
    protected void createGroupSpinner() {
        Spinner groupSpinner = findViewById(R.id.activity_registration_sp_groups);

        List<String> listAdapter = new ArrayList<>();
        if (!Groups.groupsLists.isEmpty()) {
            progressBar.setVisibility(View.GONE);

            //добавляем в массив из класса Groups группы
            for (int j = 0; j < Groups.groupsLists.size(); ++j)
                listAdapter.add(Groups.groupsLists.get(j).GroupName);
        }
        else {
            groupSpinner.setAdapter(null);
            progressBar.setVisibility(View.VISIBLE);
        }

        //устанавливаем спинер выбора групп
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_registration_layout, listAdapter);
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_white);
        groupSpinner.setAdapter(dataAdapter);

        //устанавливаем спинер
        groupSpinner.setOnItemSelectedListener(this);
    }

    //создание спиннера курсов
    protected void createCourseSpinner() {
        Spinner courseSpinner = findViewById(R.id.activity_registration_sp_courses);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(
                this,
                R.array.courses,
                R.layout.spinner_registration_layout
        );

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_white);
        courseSpinner.setAdapter(adapter);
        courseSpinner.setOnItemSelectedListener(this);
    }

    //выбор элемента на спинере (используется свитч для определения на каком спинере был выбран элемент)
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.activity_registration_sp_courses:
                Groups.groupsLists.clear();
                //загружаются группы на спинер в зависимости от курса
                Groups.downloadFromServer(this, Integer.parseInt(parent.getItemAtPosition(position).toString()), new Callable<Void>() {
                    @Override
                    public Void call() {
                        createGroupSpinner();
                        return null;
                    }
                });
                break;
            case R.id.activity_registration_sp_groups:
                this.id = id;
                break;
        }
    }

    //нужен для реализации интерфейса AdapterView.OnItemSelectedListener
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    //переход на форму логина
    public void OnClickLogin() {
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(RegistrationActivity.this);
        startActivity(new Intent(this, LoginActivity.class), options.toBundle());
    }

    //кнопка перехода в логин
    protected void createClickableSpan() {
        TextView text = findViewById(R.id.activity_registration_span2);

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