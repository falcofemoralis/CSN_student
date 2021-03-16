package com.BSLCommunity.CSN_student.Views.Fragments;

import android.content.Context;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.BSLCommunity.CSN_student.Models.Timer;
import com.BSLCommunity.CSN_student.Presenters.MainPresenter;
import com.BSLCommunity.CSN_student.R;
import com.BSLCommunity.CSN_student.ViewInterfaces.MainView;
import com.BSLCommunity.CSN_student.Views.OnFragmentInteractionListener;

import java.util.Locale;

public class MainFragment extends Fragment implements View.OnTouchListener, MainView {
    private MainPresenter mainPresenter;
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

        setRetainInstance(true);

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

            if (id == R.id.activity_main_bt_subjects) {
                nextFragment = new SubjectListFragment();
            }
            else if (id == R.id.activity_main_bt_auditorium) {
                nextFragment = new AuditoriumFragment();
            }
            else if (id == R.id.activity_main_bt_lessonsShedule) {
                nextFragment = new ScheduleFragment();
            }
            else if (id == R.id.activity_main_bt_settings) {
                nextFragment = new SettingsFragment();
            }
            else if (id == R.id.activity_main_bt_teachersSchedule) {
                nextFragment = new ScheduleFragment();
            }
            else if (id == R.id.activity_main_bt_schedule_bell) {
                nextFragment = new ScheduleBell();
            }

            transitionDrawable.reverseTransition(100);
            view.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.btn_unpressed));
            fragmentListener.onFragmentInteraction(this, nextFragment, OnFragmentInteractionListener.Action.NEXT_FRAGMENT_HIDE, null, null);
        }

        return true;
    }

}
