package com.BSLCommunity.CSN_student.Views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.BSLCommunity.CSN_student.Managers.AnimationManager;
import com.BSLCommunity.CSN_student.Models.Subject;
import com.BSLCommunity.CSN_student.Models.SubjectsInfo;
import com.BSLCommunity.CSN_student.Presenters.SubjectListPresenter;
import com.BSLCommunity.CSN_student.R;
import com.BSLCommunity.CSN_student.ViewInterfaces.SubjectListView;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

public class SubjectListActivity extends BaseActivity implements SubjectListView {
    private final int SUBJECT_ROW_COUNT = 3;
    private final int SUBJECT_COLUMN_COUNT = 3;

    SubjectListPresenter subjectListPresenter;

    static class IdGenerator {
        static int n = 0;

        static int getId() {
            return n++;
        }

        static void reset() {
            n = 0;
        }
    }
    LinearLayout tableSubjects; // Лаяут всех дисциплин
    int[] progresses; // Прогресс для каждого предмета


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnimationManager.setAnimation(getWindow(), this);
        setContentView(R.layout.activity_subject_list);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        TextView courseTextView = (TextView) findViewById(R.id.activity_subject_list_tv_course);
        //courseTextView.setText(UserModel.getUserModel().course + " " + courseTextView.getText());
        tableSubjects = findViewById(R.id.activity_subject_list_ll_table_subjects);
        this.subjectListPresenter = new SubjectListPresenter(this);
    }

    @Override
    protected void onPause() {
        IdGenerator.reset();
        super.onPause();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void setTableSubjects(ArrayList<Subject> subjects) {

        for (int i = 0; i < SUBJECT_ROW_COUNT; ++i) {
            LinearLayout subjectRow = (LinearLayout) tableSubjects.getChildAt(i);

            for (int j = 0; j < SUBJECT_COLUMN_COUNT; ++j) {
                // Получение предмета по индексу и проверка конца массива предметов
                int index = (i * SUBJECT_COLUMN_COUNT) + j;
                if (index >= subjects.size()) {
                    createFullStatistics(subjectRow);
                    return;
                }

                final Subject subject = subjects.get(index);
                createSubject(subject, subjectRow);
            }
        }
    }

    /**
     * Создание кнопки дисциплины
     * @param subject - дисциплина
     * @param container - строка которая будет содержать кнопку
     */
    @SuppressLint("ClickableViewAccessibility")
    private void createSubject(final Subject subject, LinearLayout container) {
        final LinearLayout subjectView =  (LinearLayout) getLayoutInflater().inflate(R.layout.inflate_subject_bt, container, false);
        ((TextView)subjectView.findViewById(R.id.inflate_subject_tv_name)).setText(subject.name);
        ImageView subjectImgView = (ImageView) subjectView.findViewById(R.id.inflate_subject_img);
        Picasso.get().load(subject.imgPath).into(subjectImgView, new Callback() {
            @Override
            public void onSuccess() {
                subjectView.findViewById(R.id.inflate_subject_pb).setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {

            }
        });

        subjectView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                TransitionDrawable transitionDrawable = (TransitionDrawable) view.getBackground();
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    transitionDrawable.startTransition(150);
                    view.startAnimation(AnimationUtils.loadAnimation(SubjectListActivity.this, R.anim.btn_pressed));
                }
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    Intent intent = new Intent(getBaseContext(), SubjectInfoActivity.class);
                    intent.putExtra("Subject", (new Gson()).toJson(subject));
                    startActivity(intent);

                    transitionDrawable.reverseTransition(150);
                    view.startAnimation(AnimationUtils.loadAnimation(SubjectListActivity.this, R.anim.btn_unpressed));

                }
                return true;
            }
        });
        container.addView(subjectView);
    }

    /**
     * Создание кнопки полной статистики
     * @param container - строка которая будет содержать кнопку
     */
    @SuppressLint("ClickableViewAccessibility")
    private void createFullStatistics(LinearLayout container) {
        final LinearLayout subjectView =  (LinearLayout) getLayoutInflater().inflate(R.layout.inflate_subject_bt, container, false);
        ((TextView)subjectView.findViewById(R.id.inflate_subject_tv_name)).setText(R.string.full_statistic);
        ImageView subjectImgView = (ImageView) subjectView.findViewById(R.id.inflate_subject_img);
        subjectImgView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_statistics, null));
        subjectView.findViewById(R.id.inflate_subject_pb).setVisibility(View.GONE);

        subjectView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                TransitionDrawable transitionDrawable = (TransitionDrawable) view.getBackground();
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    transitionDrawable.startTransition(150);
                    view.startAnimation(AnimationUtils.loadAnimation(SubjectListActivity.this, R.anim.btn_pressed));
                }
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    Intent intent = new Intent(getBaseContext(), SubjectInfoFullStatisticActivity.class);
                    startActivity(intent);

                    transitionDrawable.reverseTransition(150);
                    view.startAnimation(AnimationUtils.loadAnimation(SubjectListActivity.this, R.anim.btn_unpressed));

                }
                return true;
            }
        });
        container.addView(subjectView);
    }

    @Override
    public void setCourse(int course) {
        TextView tvCourse = findViewById(R.id.activity_subject_list_tv_course);
        tvCourse.setText(String.format(Locale.getDefault(), "%s %d", tvCourse.getText(), course) );
    }

    // Устанавливаем прогресс внизу экрана
    public void setProgress() {
        SubjectsInfo subjectsInfo = SubjectsInfo.getInstance(this);

        progresses = new int[subjectsInfo.subjectInfo.length];
        int sumProgress = 0; // Общий прогресс (сумма процентов каждой дисциплины)

        // Подсчет процента прогресса каждой дисциплины
        for (int i = 0; i < subjectsInfo.subjectInfo.length; ++i) {
            progresses[i] = subjectsInfo.subjectInfo[i].calculateProgress();
            sumProgress += progresses[i];
        }

        // Подсчитываем общий процент и выводим на экран
        Button progress = (Button) findViewById(R.id.activity_subject_list_bt_progress);

        try {
            progress.setText(sumProgress / progresses.length + "%");
        } catch (Exception e) {
            System.out.println(e.toString());
            progress.setText("0%");
        }

        // Обновление прогресса всех
        updateProgresses();
    }

    // Обновление прогрессов дисциплин
    public void updateProgresses() {
//        for (int i = 0; i < subjectDrawables.length; ++i)
//            subjectDrawables[i].textProgress.setText(progresses[i] + " %");
    }
}
