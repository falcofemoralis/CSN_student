package com.BSLCommunity.CSN_student.Presenters;

import android.content.Context;

import com.BSLCommunity.CSN_student.Models.AchievementsModel;
import com.BSLCommunity.CSN_student.ViewInterfaces.AchievementsView;

import java.util.ArrayList;
import java.util.Collections;

public class AchievementsPresenter {
    private final AchievementsView achievementsView;
    private final AchievementsModel achievementsModel;
    private ArrayList<AchievementsModel.Achievement> achievements;

    public AchievementsPresenter(AchievementsView achievementsView, Context context) {
        this.achievementsView = achievementsView;
        this.achievementsModel = new AchievementsModel(context);
    }

    public void initAchievements() {
        achievements = new ArrayList<>();
        Collections.addAll(achievements, achievementsModel.achievements);
        achievementsView.setAchievements(achievements);

        calculateCompleted();
    }

    public void calculateCompleted() {
        int completedCount = 0;

        for (AchievementsModel.Achievement achievement : achievements) {
            if (achievement.completed) {
                completedCount++;
            }
        }

        achievementsView.updateProgress(completedCount, achievements.size());
    }

   // public void setCompleted()
}
