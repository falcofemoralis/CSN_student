package com.BSLCommunity.CSN_student.Views;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.BSLCommunity.CSN_student.Managers.FileManager;
import com.BSLCommunity.CSN_student.Models.Timer;
import com.BSLCommunity.CSN_student.Presenters.MainPresenter;
import com.BSLCommunity.CSN_student.Presenters.SchedulePresenter;
import com.BSLCommunity.CSN_student.R;
import com.BSLCommunity.CSN_student.ViewInterfaces.MainView;

import java.util.Locale;

public class MainActivity extends BaseActivity implements View.OnTouchListener, MainView {
    private MainPresenter mainPresenter;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        FileManager.init(getApplicationContext());

        mainPresenter = new MainPresenter(this);
        mainPresenter.checkAuth();
    }

    @Override
    public void initActivity(String groupName, int course) {
        // Установка таймера
        //таймер
        Timer timer = new Timer();
        TextView time = findViewById(R.id.activity_main_tv_timerCounter);
        //переменные таймера
        TextView timeUntil = findViewById(R.id.activity_main_tv_timer_text);
        timer.checkTimer(timeUntil, time, getResources());

        // Устновка обработчиков нажатий для кнопок
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.activity_main_ll_main);
        for (int i = 5; i < linearLayout.getChildCount(); i += 2) {
            TableRow tableRow = (TableRow) linearLayout.getChildAt(i);
            tableRow.getChildAt(0).setOnTouchListener(this);
            tableRow.getChildAt(2).setOnTouchListener(this);
        }

        // Установка текстовых полей (группы и курса)
        TextView courseTextView = findViewById(R.id.activity_main_tv_course);
        TextView groupTextView = findViewById(R.id.activity_main_tv_group);
        courseTextView.setText(String.format(Locale.getDefault(), "%d %s", course, courseTextView.getText()));
        groupTextView.setText(String.format(Locale.getDefault(), "%s %s", groupName, groupTextView.getText()));
    }

    @Override
    public void openLogin() {
        startActivity(new Intent(this, LoginActivity.class));
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        TransitionDrawable transitionDrawable = (TransitionDrawable) view.getBackground();

        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            transitionDrawable.startTransition(150);
            view.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.btn_pressed));
        }
        else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            Intent intent = null;
            int id = view.getId();

            if (id == R.id.activity_main_bt_subjects) {
                intent = new Intent(this, SubjectListActivity.class);
            }
            else if (id == R.id.activity_main_bt_auditorium) {
                intent = new Intent(this, AuditoriumActivity.class);
            }
            else if (id == R.id.activity_main_bt_lessonsShedule) {
                intent = new Intent(this, ScheduleActivity.class).putExtra("EntityTypes", SchedulePresenter.EntityTypes.GROUPS);
            }
            else if (id == R.id.activity_main_bt_settings) {
                intent = new Intent(this, SettingsActivity.class);
            }
            else if (id == R.id.activity_main_bt_teachersSchedule) {
                intent = new Intent(this, ScheduleActivity.class).putExtra("EntityTypes",  SchedulePresenter.EntityTypes.TEACHERS);
            }
            else if (id == R.id.activity_main_bt_schedule_bell) {
                intent = new Intent(this, ScheduleBell.class);
            }

            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this);
            startActivity(intent, options.toBundle());

            transitionDrawable.reverseTransition(150);
            view.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.btn_unpressed));
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
