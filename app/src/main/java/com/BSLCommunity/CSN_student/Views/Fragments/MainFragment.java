package com.BSLCommunity.CSN_student.Views.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.BSLCommunity.CSN_student.Constants.LogType;
import com.BSLCommunity.CSN_student.Constants.ProgressType;
import com.BSLCommunity.CSN_student.Constants.ScheduleType;
import com.BSLCommunity.CSN_student.Managers.LogsManager;
import com.BSLCommunity.CSN_student.Models.Entity.Timer;
import com.BSLCommunity.CSN_student.Presenters.MainPresenter;
import com.BSLCommunity.CSN_student.R;
import com.BSLCommunity.CSN_student.ViewInterfaces.MainView;
import com.BSLCommunity.CSN_student.Views.OnFragmentActionBarChangeListener;
import com.BSLCommunity.CSN_student.Views.OnFragmentInteractionListener;
import com.BSLCommunity.CSN_student.Views.decorators.AnimOnTouchListener;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainFragment extends Fragment implements View.OnTouchListener, MainView, Timer.ITimer {
    private MainPresenter mainPresenter;
    private ProgressDialog progressDialog;
    private TextView timerText, timerCounter;

    View currentFragment;
    OnFragmentInteractionListener fragmentListener;
    OnFragmentActionBarChangeListener onFragmentActionBarChangeListener;

    private final HashMap<Integer, Boolean> guestAccess = new HashMap<Integer, Boolean>() {{
        put(R.id.activity_main_bt_settings, true);
        put(R.id.activity_main_bt_auditorium, true);
        put(R.id.activity_main_bt_lessonsShedule, true);
        put(R.id.activity_main_bt_teachersSchedule, true);
        put(R.id.activity_main_bt_schedule_bell, true);

        put(R.id.activity_main_bt_subjects, false);
        put(R.id.activity_main_bt_calculator, false);
    }};

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentListener = (OnFragmentInteractionListener) context;
        onFragmentActionBarChangeListener = (OnFragmentActionBarChangeListener) context;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        currentFragment = inflater.inflate(R.layout.fragment_main, container, false);
        mainPresenter = new MainPresenter(this);
        mainPresenter.checkAuth();
        return currentFragment;
    }

    @Override
    public void initFragment(String groupName, int course) {
        //переменные таймера
        timerText = currentFragment.findViewById(R.id.activity_main_tv_timerText);
        timerCounter = currentFragment.findViewById(R.id.activity_main_tv_timerCounter);

        // Запуск таймера
        new Timer(this);

        // Устновка обработчиков нажатий для кнопок
        LinearLayout linearLayout = currentFragment.findViewById(R.id.activity_main_ll_main);
        for (int i = 5; i < linearLayout.getChildCount(); i += 2) {
            TableRow tableRow = (TableRow) linearLayout.getChildAt(i);
            tableRow.getChildAt(0).setOnTouchListener(new AnimOnTouchListener(this));
            tableRow.getChildAt(2).setOnTouchListener(new AnimOnTouchListener(this));
        }

        // Установка текстовых полей (группы и курса)
        TextView courseTextView = currentFragment.findViewById(R.id.activity_main_tv_course);
        TextView groupTextView = currentFragment.findViewById(R.id.activity_main_tv_group);
        courseTextView.setText(String.format(Locale.getDefault(), "%d %s", course, courseTextView.getText()));
        groupTextView.setText(String.format(Locale.getDefault(), "%s %s", groupName, groupTextView.getText()));
    }

    @Override
    public void showProgressDialog(final int size) {
        // Диалог прогресса скачивания файлов
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMax(size);
            progressDialog.setButton(ProgressDialog.BUTTON_POSITIVE, "Retry", (DialogInterface.OnClickListener) null);
            progressDialog.setButton(ProgressDialog.BUTTON_NEGATIVE, "Close", (DialogInterface.OnClickListener) null);
            progressDialog.setTitle(getString(R.string.download_dialog_title));
            progressDialog.show();

            Button retryBtn = progressDialog.getButton(ProgressDialog.BUTTON_POSITIVE);
            retryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainPresenter.tryDownload();
                }
            });

            Button closeBtn = progressDialog.getButton(ProgressDialog.BUTTON_NEGATIVE);
            closeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.cancel();
                }
            });
        } else {
            progressDialog.setTitle(getString(R.string.download_dialog_title));
            progressDialog.setProgress(0);
        }
        progressDialog.getButton(ProgressDialog.BUTTON_POSITIVE).setEnabled(false);
        progressDialog.getButton(ProgressDialog.BUTTON_NEGATIVE).setEnabled(false);
    }

    @Override
    public void controlProgressDialog(final ProgressType type, final boolean isFirst) {
        switch (type) {
            case SET_FAIL:
                progressDialog.setTitle(getString(R.string.failed_to_download));
                progressDialog.getButton(ProgressDialog.BUTTON_POSITIVE).setEnabled(true); // Retry
                progressDialog.getButton(ProgressDialog.BUTTON_NEGATIVE).setEnabled(!isFirst); // Close
                break;
            case UPDATE_PROGRESS:
                int curProgress = progressDialog.getProgress() + 1;
                progressDialog.setProgress(curProgress);
                break;
            case SET_OK:
                progressDialog.cancel();
                break;
        }
    }

    @Override
    public void setGuestMode() {
        currentFragment.findViewById(R.id.activity_main_tv_course).setVisibility(View.GONE);
        currentFragment.findViewById(R.id.activity_main_tv_group).setVisibility(View.GONE);

        for (Map.Entry<Integer, Boolean> item : guestAccess.entrySet()) {
            if (!item.getValue()) {
                currentFragment.findViewById(item.getKey()).setOnTouchListener(new AnimOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        Toast.makeText(getContext(), R.string.you_must_be_registered, Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }));
            }
        }
    }

    @Override
    public void openLogin() {
        fragmentListener.onFragmentInteraction(this, new LoginFragment(),
                OnFragmentInteractionListener.Action.NEXT_FRAGMENT_NO_BACK_STACK, null, null);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Fragment nextFragment = null;
        int id = view.getId();

        Bundle data = null;
        LogType logType = null;
        String logInfo = null;

        if (id == R.id.activity_main_bt_subjects) {
            nextFragment = new SubjectListFragment();
            logType = LogType.OPENED_SUBJECTS;
        } else if (id == R.id.activity_main_bt_auditorium) {
            nextFragment = new AuditoriumFragment();
            logType = LogType.OPENED_AUDS;
        } else if (id == R.id.activity_main_bt_lessonsShedule) {
            nextFragment = new ScheduleFragment();
            data = new Bundle();
            int scheduleType = ScheduleType.GROUPS.ordinal();
            data.putInt("ScheduleType", scheduleType);
            logType = LogType.OPENED_SCHEDULE;
            logInfo = String.valueOf(scheduleType);
        } else if (id == R.id.activity_main_bt_settings) {
            nextFragment = new SettingsFragment();
            logType = LogType.OPENED_SETTINGS;
        } else if (id == R.id.activity_main_bt_teachersSchedule) {
            nextFragment = new ScheduleFragment();
            data = new Bundle();
            int scheduleType = ScheduleType.TEACHERS.ordinal();
            data.putInt("ScheduleType", scheduleType);
            logType = LogType.OPENED_SCHEDULE;
            logInfo = String.valueOf(scheduleType);
        } else if (id == R.id.activity_main_bt_schedule_bell) {
            nextFragment = new ScheduleBell();
            logType = LogType.OPENED_BELLS;
        } else if (id == R.id.activity_main_bt_calculator) {
            nextFragment = new GradeCalculatorFragment();
            logType = LogType.OPENED_GRADE_CALCULATOR;
        }

        LogsManager.getInstance().updateLogs(logType, logInfo);
        fragmentListener.onFragmentInteraction(this, nextFragment, OnFragmentInteractionListener.Action.NEXT_FRAGMENT_HIDE, data, null);
        return true;
    }

    @Override
    public void updateTime(String time) {
        timerCounter.setText(time);
    }

    @Override
    public void updatePair(int pair, Timer.TimeType type) {
        String pairRome = Timer.getRomePair(pair);

        if (type == Timer.TimeType.UNTIL_START) {
            timerText.setText(getResources().getString(R.string.timeStart, pairRome));
        } else {
            timerText.setText(getResources().getString(R.string.timeUntil, pairRome));
        }
    }
}
