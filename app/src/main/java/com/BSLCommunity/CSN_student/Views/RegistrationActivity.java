package com.BSLCommunity.CSN_student.Views;

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

import com.BSLCommunity.CSN_student.Managers.AnimationManager;
import com.BSLCommunity.CSN_student.Presenters.RegPresenter;
import com.BSLCommunity.CSN_student.R;
import com.BSLCommunity.CSN_student.ViewInterfaces.RegView;
import com.github.ybq.android.spinkit.style.ThreeBounce;

import java.util.ArrayList;

// Форма регистрации пользователя
public class RegistrationActivity extends BaseActivity implements RegView, AdapterView.OnItemSelectedListener, View.OnTouchListener {

    String groupName; //выбранный код группы со спиннера
    ProgressBar groupProgressBar; //анимация загрузки в спиннере групп
    ProgressBar courseProgressBar; //анимация загрузки в спиннере курсов
    TextView courseText; // Надпись курс
    private RegPresenter regPresenter;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        AnimationManager.setAnimation(getWindow(), this);
        groupProgressBar = (ProgressBar) findViewById(R.id.activity_registration_pb_groups);
        groupProgressBar.setIndeterminateDrawable(new ThreeBounce());
        courseProgressBar = (ProgressBar) findViewById(R.id.activity_registration_pb_courses);
        courseProgressBar.setIndeterminateDrawable(new ThreeBounce());
        courseText = (TextView) findViewById(R.id.activity_registration_tv_course);

        this.createClickableSpan();
        ((Button) findViewById(R.id.activity_registration_bt_register)).setOnTouchListener(this);

        regPresenter = new RegPresenter(this);
        this.regPresenter.initSpinnerData();
    }

    /**
     * Возращает активити в исходное состояние
     */
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        TransitionDrawable transitionDrawable = (TransitionDrawable) view.getBackground();

        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            transitionDrawable.startTransition(150);
            view.startAnimation(AnimationUtils.loadAnimation(RegistrationActivity.this, R.anim.btn_pressed));
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            EditText nickName = (EditText) findViewById(R.id.activity_registration_et_nickname);
            EditText password = (EditText) findViewById(R.id.activity_registration_et_password);
            EditText repeatPassword = (EditText) findViewById(R.id.activity_registration_et_passwordRe);

            this.regPresenter.tryRegistration(nickName.getText().toString(), password.getText().toString(), repeatPassword.getText().toString(), this.groupName);

            transitionDrawable.reverseTransition(150);
            view.startAnimation(AnimationUtils.loadAnimation(RegistrationActivity.this, R.anim.btn_unpressed));
        }
        return false;
    }

    /**
     * Выбор элемента на спинере (используется свитч для определения на каком спинере был выбран элемент)
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.activity_registration_sp_courses) {
            int course = Integer.parseInt(parent.getItemAtPosition(position).toString());
            this.regPresenter.chosenCourse(course);
        } else if (parent.getId() == R.id.activity_registration_sp_groups) {
            groupName = parent.getItemAtPosition(position).toString();
        }
    }

    /**
     * Нужен для реализации интерфейса AdapterView.OnItemSelectedListener
     */
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    /**
     * Переход в активити логина
     */
    public void toLogin() {
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(RegistrationActivity.this);
        startActivity(new Intent(this, LoginActivity.class), options.toBundle());
    }

    /**
     * Создание кликабельного текста
     */
    protected void createClickableSpan() {
        TextView text = findViewById(R.id.activity_registration_span2);
        SpannableString ss = new SpannableString(text.getText());

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                toLogin();
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

    @Override
    public void showToastError(final int id) {
        Toast.makeText(getApplicationContext(), id, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void openMain() {
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void setSpinnersData(final ArrayList<String> groupNames, final ArrayList<String> courses) {
        int[] idSpins = new int[]{R.id.activity_registration_sp_groups, R.id.activity_registration_sp_courses};
        ArrayList<String>[] lists = new ArrayList[]{groupNames, courses};

        for (int i = 0; i < idSpins.length; ++i) {
            Spinner spinner = findViewById(idSpins[i]);
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_registration_layout, lists[i]);
            dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_registration);
            spinner.setAdapter(dataAdapter);
            spinner.setOnItemSelectedListener(RegistrationActivity.this);
        }
    }

    @Override
    public void setGroupNamesSpinner(ArrayList<String> groupNames) {
        Spinner spinner = findViewById(R.id.activity_registration_sp_groups);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_registration_layout, groupNames);
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_registration);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void visibilityProgressBar(final boolean show) {
        int visibility = show ? ProgressBar.VISIBLE : ProgressBar.GONE;
        groupProgressBar.setVisibility(visibility);
        courseProgressBar.setVisibility(visibility);
        courseText.setVisibility(show ? ProgressBar.GONE : ProgressBar.VISIBLE);
    }
}