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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        progressBar = (ProgressBar) findViewById(R.id.activity_registration_pb_groups);
        progressBar.setIndeterminateDrawable(new ThreeBounce());
        createCourseSpinner();
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
        EditText NickName = (EditText) findViewById(R.id.activity_registration_et_nickname);
        EditText Password = (EditText) findViewById(R.id.activity_registration_et_password);
        EditText RepeatPassword = (EditText) findViewById(R.id.activity_registration_et_passwordRe);

        if (Password.getText().toString().equals(RepeatPassword.getText().toString())) {
            User.registration(getApplicationContext(), RegistrationActivity.this, NickName.getText().toString().toLowerCase(), Password.getText().toString(), Integer.toString((Groups.groupsLists.get((int) id).id)));
        } else {
            Toast.makeText(this, R.string.inccorect_password, Toast.LENGTH_SHORT).show();
        }
    }

    //создание спиннера групп
    protected void createGroupSpinner() {
        Spinner groupSpinner = findViewById(R.id.activity_registration_sp_groups);

        List<String> listAdapter = new ArrayList<>();
        if (Groups.groupsLists.size() != 0) {
            progressBar.setVisibility(View.GONE);

            //добавляем в массив из класса Groups группы
            for (int j = 0; j < Groups.groupsLists.size(); ++j)
                listAdapter.add(Groups.groupsLists.get(j).GroupName);
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
        startActivity(new Intent(this, LoginActivity.class));
        overridePendingTransition(0, 0);
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
