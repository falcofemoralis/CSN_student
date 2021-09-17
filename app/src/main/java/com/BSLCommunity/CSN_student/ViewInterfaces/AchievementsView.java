package com.BSLCommunity.CSN_student.ViewInterfaces;

import com.BSLCommunity.CSN_student.Models.AchievementsModel;

import java.util.ArrayList;

public interface AchievementsView {
    void setAchievements(ArrayList<AchievementsModel.Achievement> achievements);

    void updateProgress(int count, int max);
}
