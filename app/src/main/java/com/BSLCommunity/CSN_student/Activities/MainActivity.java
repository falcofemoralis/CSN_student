package com.BSLCommunity.CSN_student.Activities;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.BSLCommunity.CSN_student.Activities.Schedule.ScheduleActivity;
import com.BSLCommunity.CSN_student.Objects.Groups;
import com.BSLCommunity.CSN_student.Objects.LocalData;
import com.BSLCommunity.CSN_student.Objects.Subjects;
import com.BSLCommunity.CSN_student.Objects.Teachers;
import com.BSLCommunity.CSN_student.Objects.Timer;
import com.BSLCommunity.CSN_student.Objects.User;
import com.BSLCommunity.CSN_student.R;
import java.util.concurrent.Callable;

import static com.BSLCommunity.CSN_student.Objects.Settings.encryptedSharedPreferences;
import static com.BSLCommunity.CSN_student.Objects.Settings.setSettingsFile;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {
    public static String MAIN_URL = "http://a0459938.xsph.ru/";
    public static String GROUP_FILE_NAME = "groups";

    Timer timer = new Timer(); //таймер
    TextView Time, TimeUntil; //переменные таймера

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.activity_main_ll_main);
        for (int i = 5; i < linearLayout.getChildCount(); i += 2) {
            TableRow tableRow = (TableRow) linearLayout.getChildAt(i);
            tableRow.getChildAt(0).setOnTouchListener(this);
            tableRow.getChildAt(2).setOnTouchListener(this);
        }

        Time = (TextView) findViewById(R.id.activity_main_tv_timerCounter);
        TimeUntil = (TextView) findViewById(R.id.activity_main_tv_timer_text);

        setSettingsFile(this);

        Boolean is_registered = encryptedSharedPreferences.getBoolean(SettingsActivity.KEY_IS_REGISTERED, false);
        if (!is_registered) {
            startActivity(new Intent(this, LoginActivity.class));
            return;
        } else {
            TextView courseTextView = (TextView) findViewById(R.id.activity_main_tv_course);
            TextView groupTextView = (TextView) findViewById(R.id.activity_main_tv_group);

            courseTextView.setText(String.valueOf(User.getInstance().course) + " Course");
            groupTextView.setText(User.getInstance().nameGroup + " Group");
        }

        // Скачиваем все необходимые апдейт листы для проверки актуальности данных и проверяем данные
        LocalData.downloadUpdateList(getApplicationContext(), LocalData.updateListGroups, LocalData.TypeData.groups, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Groups.init(getApplicationContext(), User.getInstance().course, new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        LocalData.checkUpdate(getApplicationContext(), LocalData.TypeData.groups);
                        return null;
                    }
                });
                return null;
            }
        });

        LocalData.downloadUpdateList(getApplicationContext(), LocalData.updateListTeachers, LocalData.TypeData.teachers, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Teachers.init(getApplicationContext(), new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        LocalData.checkUpdate(getApplicationContext(), LocalData.TypeData.teachers);
                        return null;
                    }
                });
                return null;
            }
        });

        Subjects.init(getApplicationContext(), new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                return null;
            }
        });
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        TransitionDrawable transitionDrawable = (TransitionDrawable) view.getBackground();

        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            transitionDrawable.startTransition(150);
            view.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.btn_pressed));
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            Intent intent = null;
            switch (view.getId()) {
                case R.id.activity_main_bt_subjects:
                    intent = new Intent(this, SubjectListActivity.class);
                    break;
                case R.id.activity_main_bt_rating:
                    intent = new Intent(this, RatingActivity.class);
                    break;
                case R.id.activity_main_bt_lessonsShedule:
                    intent = new Intent(this, ScheduleActivity.class).putExtra("typeSchedule", "Groups");
                    break;
                case R.id.activity_main_bt_settings:
                    intent = new Intent(this, SettingsActivity.class);
                    break;
                case R.id.activity_main_bt_teachersSchedule:
                    intent = new Intent(this, ScheduleActivity.class).putExtra("typeSchedule", "Teachers");
                    break;
            }

            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this);
            startActivity(intent, options.toBundle());

            transitionDrawable.reverseTransition(150);
            view.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.btn_unpressed));
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        timer.checkTimer(TimeUntil, Time, getResources());
    }

    @Override
    protected void onPause() {
        timer.resetTimer();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}