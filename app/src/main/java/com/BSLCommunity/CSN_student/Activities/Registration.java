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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.BSLCommunity.CSN_student.Objects.Groups;
import com.BSLCommunity.CSN_student.R;
import com.BSLCommunity.CSN_student.R;
import com.BSLCommunity.CSN_student.Objects.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.BSLCommunity.CSN_student.Objects.Groups.getGroups;
import static com.BSLCommunity.CSN_student.Objects.Settings.encryptedSharedPreferences;

// Форма регистрации пользователя
public class Registration extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Spinner groupSpinner; //спиннер группы
    long id; //выбранный код группы со спиннера

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        createCourseSpinner();
        createGroupSpinner();
        createClickableSpan();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    //возращает активити в исходное состояние
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    //обработчик регистрации юзера
    public void OnClick(View view) {
        EditText NickName = (EditText) findViewById(R.id.Nick);
        EditText Password = (EditText) findViewById(R.id.pass);
        EditText RepeatPassword = (EditText) findViewById(R.id.checkPass);

        if (Password.getText().toString().equals(RepeatPassword.getText().toString())) {
            User.registration(getApplicationContext(), Registration.this, NickName.getText().toString().toLowerCase(), Password.getText().toString(),String.valueOf(Groups.getInstance(this).groupsLists[(int)id].id));
        } else {
            Toast.makeText(this, R.string.inccorect_password, Toast.LENGTH_SHORT).show();
        }
    }

    //создание спиннера групп
    protected void createGroupSpinner() {
        groupSpinner = findViewById(R.id.group);

        //устанавливаем спинер
        groupSpinner.setOnItemSelectedListener(this);
    }

    //создание спиннера курсов
    protected void createCourseSpinner() {
        Spinner courseSpinner = findViewById(R.id.course);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(
                this,
                R.array.courses,
                R.layout.color_spinner_layout
        );

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_schedule);
        courseSpinner.setAdapter(adapter);
        courseSpinner.setOnItemSelectedListener(this);
    }

    //выбор элемента на спинере (используется свитч для определения на каком спинере был выбран элемент)
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.course:
                //загружаются группы на спинер в зависимости от курса
                getGroups(this, Integer.parseInt(parent.getItemAtPosition(position).toString()), groupSpinner, R.layout.color_spinner_layout);
                break;
            case R.id.group:
                //+1 т.к спиннер хранит группы от 0, а в базе от 1
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
        startActivity(new Intent(this, Login.class));
        overridePendingTransition(0, 0);
    }

    //кнопка перехода в логин
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
