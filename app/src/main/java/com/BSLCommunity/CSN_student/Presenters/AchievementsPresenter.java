package com.BSLCommunity.CSN_student.Presenters;

import com.BSLCommunity.CSN_student.Models.AchievementsModel;
import com.BSLCommunity.CSN_student.Models.UserData;
import com.BSLCommunity.CSN_student.ViewInterfaces.AchievementsView;

import java.util.ArrayList;

public class AchievementsPresenter {
    private final AchievementsView achievementsView;
    private final AchievementsModel achievementsModel;
    private final UserData userData;

    public AchievementsPresenter(AchievementsView achievementsView) {
        this.achievementsView = achievementsView;
        this.achievementsModel = AchievementsModel.getAchievementsModel();
        this.userData = UserData.getUserData();
    }

    public void initAchievements() {
        if (userData.userAchievements == null) {
            userData.userAchievements = new ArrayList<>();
        }
        achievementsView.setAchievements(achievementsModel.achievements);
        calculateCompleted();
    }

    public void calculateCompleted() {
        int completedCount = 0;
        for (AchievementsModel.UserAchievement achievement : userData.userAchievements) {
            if (achievement.isCompleted) {
                completedCount++;
            }
        }

        achievementsView.updateProgress(completedCount, achievementsModel.achievements.size());
    }

    public void save() {
        UserData.getUserData().saveData();
    }

    public AchievementsModel.UserAchievement findUserAchievementById(int id) {
        for (AchievementsModel.UserAchievement achievement : userData.userAchievements) {
            if (achievement.id == id) {
                return achievement;
            }
        }

        return new AchievementsModel.UserAchievement(id, false);
    }

    public AchievementsModel.UserAchievement updateUserAchievement(AchievementsModel.UserAchievement userAchievement) {
        userAchievement.isCompleted = !userAchievement.isCompleted;
        boolean isExist = false;

        for (AchievementsModel.UserAchievement lua : userData.userAchievements) {
            if (lua.id == userAchievement.id) {
                //return userAchievement;
                isExist = true;
            }
        }
        if (!isExist)
            userData.userAchievements.add(userAchievement);
        calculateCompleted();
        return userAchievement;
    }
}
