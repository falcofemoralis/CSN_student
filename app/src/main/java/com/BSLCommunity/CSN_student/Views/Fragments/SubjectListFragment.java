package com.BSLCommunity.CSN_student.Views.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.BSLCommunity.CSN_student.Constants.ActionBarType;
import com.BSLCommunity.CSN_student.Constants.LogType;
import com.BSLCommunity.CSN_student.Managers.LogsManager;
import com.BSLCommunity.CSN_student.Models.Entity.EditableSubject;
import com.BSLCommunity.CSN_student.Models.Entity.Subject;
import com.BSLCommunity.CSN_student.Models.SubjectModel;
import com.BSLCommunity.CSN_student.Presenters.SubjectListPresenter;
import com.BSLCommunity.CSN_student.R;
import com.BSLCommunity.CSN_student.ViewInterfaces.SubjectListView;
import com.BSLCommunity.CSN_student.Views.OnFragmentActionBarChangeListener;
import com.BSLCommunity.CSN_student.Views.OnFragmentInteractionListener;
import com.BSLCommunity.CSN_student.Views.decorators.AnimOnTouchListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Locale;

public class SubjectListFragment extends Fragment implements SubjectListView {
    private final int SUBJECT_COLUMN_COUNT = 3;

    SubjectListPresenter subjectListPresenter;

    LinearLayout tableSubjects; // Лаяут всех дисциплин
    ArrayList<LinearLayout> subjectViews;
    LinearLayout fullStatView;
    Fragment thisFragment = this;

    View currentFragment;
    OnFragmentInteractionListener fragmentListener;
    OnFragmentActionBarChangeListener actionBarChangeListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentListener = (OnFragmentInteractionListener) context;
        actionBarChangeListener = (OnFragmentActionBarChangeListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        currentFragment = inflater.inflate(R.layout.fragment_subject_list, container, false);

        tableSubjects = currentFragment.findViewById(R.id.activity_subject_list_ll_table_subjects);
        subjectViews = new ArrayList<>();
        this.subjectListPresenter = new SubjectListPresenter(this);

        return currentFragment;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden && getActivity().getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onResume() {
        this.subjectListPresenter.recalculateProgresses();
        actionBarChangeListener.setActionBarColor(R.color.dark_blue, ActionBarType.STATUS_BAR);
        actionBarChangeListener.setActionBarColor(R.color.dark_red, ActionBarType.NAVIGATION_BAR);
        super.onResume();
    }

    /**
     * Отрисовка всей таблицы дисциплин
     * @param editableSubjects - дисциплины с информацией пользователя о прогрессе
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void setTableSubjects(ArrayList<EditableSubject> editableSubjects) {
        SubjectModel subjectModel = SubjectModel.getSubjectModel();
        final int rowCount = (int) Math.ceil((editableSubjects.size() + 1) * 1.0f / SUBJECT_COLUMN_COUNT);

        for (int i = 0; i < rowCount; ++i) {
            LinearLayout subjectRow =  (LinearLayout) getLayoutInflater().inflate(R.layout.inflate_subject_list_row, tableSubjects, false);

            for (int j = 0; j < SUBJECT_COLUMN_COUNT; ++j) {
                // Получение предмета по индексу и проверка конца массива предметов
                int index = (i * SUBJECT_COLUMN_COUNT) + j;
                if (index >= editableSubjects.size()) {
                    createFullStatistics(subjectRow);
                    break;
                }

                final EditableSubject editableSubject = editableSubjects.get(index);
                final Subject subject = subjectModel.findById(editableSubject.idSubject);
                createSubject(editableSubject, subject, subjectRow);
            }

            tableSubjects.addView(subjectRow);
        }
    }

    /**
     * Создание кнопки дисциплины
     *
     * @param editableSubject - дисциплина
     * @param container       - строка которая будет содержать кнопку
     */
    @SuppressLint("ClickableViewAccessibility")
    private void createSubject(final EditableSubject editableSubject, final Subject subject, LinearLayout container) {
        final LinearLayout subjectView = (LinearLayout) getLayoutInflater().inflate(R.layout.inflate_subject_bt, container, false);

        // Установка имени дисциплины
        TextView subjectNameTV = subjectView.findViewById(R.id.inflate_subject_tv_name);
        subjectNameTV.setText(subject.abb);

        ((TextView) subjectView.findViewById(R.id.inflate_subject_tv_percent_progress)).setText(editableSubject.calculateProgress() + "%");
        ImageView subjectImgView = subjectView.findViewById(R.id.inflate_subject_img);
        BitmapDrawable img = subject.getSubjectImage(getContext());

        if (img != null) {
            subjectImgView.setImageDrawable(img);
        } else {
            subjectImgView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_no_subjecticon));
        }
        subjectView.findViewById(R.id.inflate_subject_pb).setVisibility(View.GONE);

        subjectView.findViewById(R.id.inflate_subject_rl_card).setOnTouchListener(new AnimOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Bundle data = new Bundle();
                data.putString("Subject", new Gson().toJson(editableSubject));
                LogsManager.getInstance().updateLogs(LogType.OPENED_SUBJECT, String.valueOf(editableSubject.idSubject));
                fragmentListener.onFragmentInteraction(thisFragment, new SubjectEditorFragment(),
                        OnFragmentInteractionListener.Action.NEXT_FRAGMENT_HIDE, data, null);
                return true;
            }
        }));

        subjectViews.add(subjectView);
        container.addView(subjectView);
    }

    /**
     * Создание кнопки полной статистики
     *
     * @param container - строка которая будет содержать кнопку
     */
    @SuppressLint("ClickableViewAccessibility")
    private void createFullStatistics(LinearLayout container) {

        final LinearLayout fullStatView = (LinearLayout) getLayoutInflater().inflate(R.layout.inflate_subject_bt, container, false);
        ((TextView) fullStatView.findViewById(R.id.inflate_subject_tv_name)).setText(R.string.full_statistic);
        ImageView subjectImgView = (ImageView) fullStatView.findViewById(R.id.inflate_subject_img);

        subjectImgView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_statistics, null));
        fullStatView.findViewById(R.id.inflate_subject_pb).setVisibility(View.GONE);

        fullStatView.findViewById(R.id.inflate_subject_rl_card).setOnTouchListener(new AnimOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                LogsManager.getInstance().updateLogs(LogType.ENTER_FULL_STAT);

                fragmentListener.onFragmentInteraction(thisFragment, new FullStatFragment(),
                        OnFragmentInteractionListener.Action.NEXT_FRAGMENT_HIDE, null, null);

                return true;
            }
        }));

        this.fullStatView = fullStatView;
        container.addView(fullStatView);
    }

    /**
     * Установка курса пользователя
     * @param course - номер курса
     */
    @Override
    public void setCourse(int course) {
        TextView tvCourse = currentFragment.findViewById(R.id.activity_subject_list_tv_course);
        tvCourse.setText(String.format(Locale.getDefault(), "%s %d", tvCourse.getText(), course));
    }

    /**
     * Обновление прогресса на кнопках дисциплин
     * @param subjectProgresses - прогрессы по дисциплинам
     * @param sumProgress - суммарный прогресс
     */
    @Override
    public void updateProgresses(int[] subjectProgresses, int sumProgress) {
        for (int i = 0; i < subjectViews.size(); ++i) {
            ((TextView) subjectViews.get(i).findViewById(R.id.inflate_subject_tv_percent_progress)).setText(subjectProgresses[i] + "%");
        }
        ((TextView) fullStatView.findViewById(R.id.inflate_subject_tv_percent_progress)).setText(sumProgress + "%");
        ((Button) currentFragment.findViewById(R.id.activity_subject_list_bt_progress)).setText(sumProgress + "%");
    }

    @Override
    public void onPause() {
        actionBarChangeListener.setActionBarColor(R.color.background, ActionBarType.STATUS_BAR);
        actionBarChangeListener.setActionBarColor(R.color.background, ActionBarType.NAVIGATION_BAR);
        super.onPause();
    }
}

