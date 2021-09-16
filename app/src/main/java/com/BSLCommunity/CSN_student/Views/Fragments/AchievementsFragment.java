package com.BSLCommunity.CSN_student.Views.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.BSLCommunity.CSN_student.App;
import com.BSLCommunity.CSN_student.Constants.ActionBarType;
import com.BSLCommunity.CSN_student.Models.AchievementsModel;
import com.BSLCommunity.CSN_student.Presenters.AchievementsPresenter;
import com.BSLCommunity.CSN_student.R;
import com.BSLCommunity.CSN_student.ViewInterfaces.AchievementsView;
import com.BSLCommunity.CSN_student.Views.OnFragmentActionBarChangeListener;

import java.util.ArrayList;

public class AchievementsFragment extends Fragment implements AchievementsView {
    private View currentFragment;
    private AchievementsPresenter achievementsPresenter;
    private OnFragmentActionBarChangeListener onFragmentActionBarChangeListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onFragmentActionBarChangeListener = (OnFragmentActionBarChangeListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        currentFragment = inflater.inflate(R.layout.fragment_achievements, container, false);

        achievementsPresenter = new AchievementsPresenter(this);
        achievementsPresenter.initAchievements();

        return currentFragment;
    }

    @Override
    public void onResume() {
        initActionBar();
        super.onResume();
    }

    @Override
    public void onPause() {
        achievementsPresenter.save();
        onFragmentActionBarChangeListener.setActionBarColor(R.color.background, ActionBarType.STATUS_BAR);
        onFragmentActionBarChangeListener.setActionBarColor(R.color.background, ActionBarType.NAVIGATION_BAR);
        super.onPause();
    }

    public void initActionBar() {
        onFragmentActionBarChangeListener.setActionBarColor(R.color.dark_blue, ActionBarType.STATUS_BAR);
        onFragmentActionBarChangeListener.setActionBarColor(R.color.dark_red, ActionBarType.NAVIGATION_BAR);
    }

    @Override
    public void setAchievements(ArrayList<AchievementsModel.Achievement> achievements) {
        RecyclerView recyclerView = currentFragment.findViewById(R.id.achievements_fragment_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(App.getApp().context()));
        recyclerView.setAdapter(new AchievementsAdapter(requireContext(), achievements, achievementsPresenter));
    }

    @Override
    public void updateProgress(int count, int max) {
        Button counter = currentFragment.findViewById(R.id.achievements_fragment_tv_counter);
        counter.setText(count + "/" + max);
    }
}