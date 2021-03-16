package com.BSLCommunity.CSN_student.Views.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.BSLCommunity.CSN_student.Constants.ProgressType;
import com.BSLCommunity.CSN_student.Constants.ScheduleType;
import com.BSLCommunity.CSN_student.Models.Timer;
import com.BSLCommunity.CSN_student.Presenters.MainPresenter;
import com.BSLCommunity.CSN_student.R;
import com.BSLCommunity.CSN_student.ViewInterfaces.MainView;
import com.BSLCommunity.CSN_student.Views.OnFragmentInteractionListener;

import java.util.Locale;

public class MainFragment extends Fragment implements View.OnTouchListener, MainView {
    private MainPresenter mainPresenter;
    private ProgressDialog progressDialog;

    View currentFragment;
    OnFragmentInteractionListener fragmentListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentListener = (OnFragmentInteractionListener) context;
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
    public void initActivity(String groupName, int course) {
        // Установка таймера
        //таймер
        Timer timer = new Timer();
        TextView time = currentFragment.findViewById(R.id.activity_main_tv_timerCounter);
        //переменные таймера
        TextView timeUntil = currentFragment.findViewById(R.id.activity_main_tv_timer_text);
        timer.checkTimer(timeUntil, time, getResources());

        // Устновка обработчиков нажатий для кнопок
        LinearLayout linearLayout = currentFragment.findViewById(R.id.activity_main_ll_main);
        for (int i = 5; i < linearLayout.getChildCount(); i += 2) {
            TableRow tableRow = (TableRow) linearLayout.getChildAt(i);
            tableRow.getChildAt(0).setOnTouchListener(this);
            tableRow.getChildAt(2).setOnTouchListener(this);
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
                progressDialog.setTitle("Failed to download");
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
    public void openLogin() {
        fragmentListener.onFragmentInteraction(this, new LoginFragment(),
                OnFragmentInteractionListener.Action.NEXT_FRAGMENT_HIDE, null, null);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        TransitionDrawable transitionDrawable = (TransitionDrawable) view.getBackground();

        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            transitionDrawable.startTransition(150);
            view.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.btn_pressed));
        }
        else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            Fragment nextFragment = null;
            int id = view.getId();

            Bundle data = null;

            if (id == R.id.activity_main_bt_subjects) {
                nextFragment = new SubjectListFragment();

            }
            else if (id == R.id.activity_main_bt_auditorium) {
                nextFragment = new AuditoriumFragment();
            }
            else if (id == R.id.activity_main_bt_lessonsShedule) {
                nextFragment = new ScheduleFragment();
                data = new Bundle();
                data.putInt("ScheduleType", ScheduleType.GROUPS.ordinal());
            }
            else if (id == R.id.activity_main_bt_settings) {
                nextFragment = new SettingsFragment();
            }
            else if (id == R.id.activity_main_bt_teachersSchedule) {
                nextFragment = new ScheduleFragment();
                data = new Bundle();
                data.putInt("ScheduleType", ScheduleType.TEACHERS.ordinal());
            }
            else if (id == R.id.activity_main_bt_schedule_bell) {
                nextFragment = new ScheduleBell();
            }

            transitionDrawable.reverseTransition(100);
            view.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.btn_unpressed));
            fragmentListener.onFragmentInteraction(this, nextFragment, OnFragmentInteractionListener.Action.NEXT_FRAGMENT_HIDE, data, null);
        }

        return true;
    }

}
