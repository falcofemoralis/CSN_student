package com.BSLCommunity.CSN_student.Views.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.BSLCommunity.CSN_student.Models.AchievementsModel;
import com.BSLCommunity.CSN_student.Presenters.AchievementsPresenter;
import com.BSLCommunity.CSN_student.R;
import com.BSLCommunity.CSN_student.ViewInterfaces.AchievementsView;

import java.util.ArrayList;

public class AchievementsFragment extends Fragment implements AchievementsView {
    private View currentFragment;
    private AchievementsPresenter achievementsPresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        currentFragment = inflater.inflate(R.layout.fragment_achievements, container, false);

        achievementsPresenter = new AchievementsPresenter(this, requireContext());
        achievementsPresenter.initAchievements();

        return currentFragment;
    }

    @Override
    public void setAchievements(ArrayList<AchievementsModel.Achievement> achievements) {
        RecyclerView recyclerView = currentFragment.findViewById(R.id.achievements_fragment_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(new AchievementsAdapter(requireContext(), achievements, achievementsPresenter));
    }

    @Override
    public void updateProgress(int count, int max) {
        TextView counter = currentFragment.findViewById(R.id.achievements_fragment_tv_counter);
        counter.setText(count + "/" + max);
    }
}