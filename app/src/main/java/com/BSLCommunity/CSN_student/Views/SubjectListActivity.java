package com.BSLCommunity.CSN_student.Views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.BSLCommunity.CSN_student.Managers.AnimationManager;
import com.BSLCommunity.CSN_student.Managers.LocaleHelper;
import com.BSLCommunity.CSN_student.Models.EditableSubject;
import com.BSLCommunity.CSN_student.Presenters.SubjectListPresenter;
import com.BSLCommunity.CSN_student.R;
import com.BSLCommunity.CSN_student.ViewInterfaces.SubjectListView;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class SubjectListActivity extends BaseActivity implements SubjectListView {
    private final int SUBJECT_ROW_COUNT = 3;
    private final int SUBJECT_COLUMN_COUNT = 3;

    SubjectListPresenter subjectListPresenter;

    LinearLayout tableSubjects; // Лаяут всех дисциплин
    ArrayList<LinearLayout> subjectViews;
    LinearLayout fullStatView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnimationManager.setAnimation(getWindow(), this);
        setContentView(R.layout.activity_subject_list);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        tableSubjects = findViewById(R.id.activity_subject_list_ll_table_subjects);
        subjectViews = new ArrayList<>();
        this.subjectListPresenter = new SubjectListPresenter(this);
    }

    @Override
    protected void onResume() {
        this.subjectListPresenter.recalculateProgresses();
        super.onResume();
    }

    /**
     * @see SubjectListView
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void setTableSubjects(ArrayList<EditableSubject> editableSubjects) {

        for (int i = 0; i < SUBJECT_ROW_COUNT; ++i) {
            LinearLayout subjectRow = (LinearLayout) tableSubjects.getChildAt(i);

            for (int j = 0; j < SUBJECT_COLUMN_COUNT; ++j) {
                // Получение предмета по индексу и проверка конца массива предметов
                int index = (i * SUBJECT_COLUMN_COUNT) + j;
                if (index >= editableSubjects.size()) {
                    createFullStatistics(subjectRow);
                    return;
                }

                final EditableSubject subject = editableSubjects.get(index);
                createSubject(subject, subjectRow);
            }
        }
    }

    /**
     * Создание кнопки дисциплины
     * @param editableSubject - дисциплина
     * @param container - строка которая будет содержать кнопку
     */
    @SuppressLint("ClickableViewAccessibility")
    private void createSubject(final EditableSubject editableSubject, LinearLayout container) {
        final LinearLayout subjectView =  (LinearLayout) getLayoutInflater().inflate(R.layout.inflate_subject_bt, container, false);

        // Установка имени дисциплины
        try {
            String subjectName = new JSONObject(editableSubject.name).getString(LocaleHelper.getLanguage(this));
            ((TextView)subjectView.findViewById(R.id.inflate_subject_tv_name)).setText(subjectName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ImageView subjectImgView = (ImageView) subjectView.findViewById(R.id.inflate_subject_img);
        Picasso.get().load(editableSubject.imgPath).into(subjectImgView, new Callback() {
            @Override
            public void onSuccess() {
                subjectView.findViewById(R.id.inflate_subject_pb).setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {

            }
        });
        subjectView.findViewById(R.id.inflate_subject_rl_card).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                TransitionDrawable transitionDrawable = (TransitionDrawable) view.getBackground();
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    transitionDrawable.startTransition(150);
                    view.startAnimation(AnimationUtils.loadAnimation(SubjectListActivity.this, R.anim.btn_pressed));
                }
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    Intent intent = new Intent(getBaseContext(), SubjectEditorActivity.class);
                    intent.putExtra("Subject", (new Gson()).toJson(editableSubject));
                    startActivity(intent);

                    transitionDrawable.reverseTransition(150);
                    view.startAnimation(AnimationUtils.loadAnimation(SubjectListActivity.this, R.anim.btn_unpressed));

                }
                return true;
            }
        });

        subjectViews.add(subjectView);
        container.addView(subjectView);
    }

    /**
     * Создание кнопки полной статистики
     * @param container - строка которая будет содержать кнопку
     */
    @SuppressLint("ClickableViewAccessibility")
    private void createFullStatistics(LinearLayout container) {
        final LinearLayout fullStatView =  (LinearLayout) getLayoutInflater().inflate(R.layout.inflate_subject_bt, container, false);
        ((TextView)fullStatView.findViewById(R.id.inflate_subject_tv_name)).setText(R.string.full_statistic);
        ImageView subjectImgView = (ImageView) fullStatView.findViewById(R.id.inflate_subject_img);
        subjectImgView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_statistics, null));
        fullStatView.findViewById(R.id.inflate_subject_pb).setVisibility(View.GONE);

        fullStatView.findViewById(R.id.inflate_subject_rl_card).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                TransitionDrawable transitionDrawable = (TransitionDrawable) view.getBackground();
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    transitionDrawable.startTransition(150);
                    view.startAnimation(AnimationUtils.loadAnimation(SubjectListActivity.this, R.anim.btn_pressed));
                }
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    Intent intent = new Intent(getBaseContext(), FullStatActivity.class);
                    startActivity(intent);

                    transitionDrawable.reverseTransition(150);
                    view.startAnimation(AnimationUtils.loadAnimation(SubjectListActivity.this, R.anim.btn_unpressed));

                }
                return true;
            }
        });

        this.fullStatView = fullStatView;
        container.addView(fullStatView);
    }

    /**
     * @see SubjectListView
     */
    @Override
    public void setCourse(int course) {
        TextView tvCourse = findViewById(R.id.activity_subject_list_tv_course);
        tvCourse.setText(String.format(Locale.getDefault(), "%s %d", tvCourse.getText(), course) );
    }

    /**
     * @see SubjectListView
     */
    @Override
    public void updateProgresses(int[] subjectProgresses, int sumProgress) {
        for (int i = 0; i < subjectViews.size(); ++i) {
            ((TextView)subjectViews.get(i).findViewById(R.id.inflate_subject_tv_percent_progress)).setText(subjectProgresses[i] + "%");
        }
        if (fullStatView != null)
            ((TextView)fullStatView.findViewById(R.id.inflate_subject_tv_percent_progress)).setText(sumProgress + "%");
    }
}
