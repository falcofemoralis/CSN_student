package com.BSLCommunity.CSN_student.Views.Fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.BSLCommunity.CSN_student.Models.AchievementsModel;
import com.BSLCommunity.CSN_student.Presenters.AchievementsPresenter;
import com.BSLCommunity.CSN_student.R;

import java.util.ArrayList;

public class AchievementsAdapter extends RecyclerView.Adapter<AchievementsAdapter.ViewHolder> {
    private final LayoutInflater inflater;
    private final ArrayList<AchievementsModel.Achievement> achievements;
    private final Context context;
    private final AchievementsPresenter achievementsPresenter;

    public AchievementsAdapter(Context context, ArrayList<AchievementsModel.Achievement> achievements, AchievementsPresenter achievementsPresenter) {
        this.context = context;
        this.achievements = achievements;
        this.inflater = LayoutInflater.from(context);
        this.achievementsPresenter = achievementsPresenter;
    }

    @NonNull
    @Override
    public AchievementsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.inflate_achievement, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final AchievementsModel.Achievement achievement = achievements.get(position);
        holder.achievementName.setText(achievement.name);
        holder.achievementInfo.setText(achievement.info);
        final AchievementsModel.UserAchievement userAchievement = achievementsPresenter.findUserAchievementById(achievement.id);
        setLayoutColor(holder, userAchievement.isCompleted);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                achievementsPresenter.updateUserAchievement(userAchievement);
                setLayoutColor(holder, userAchievement.isCompleted);
            }
        });
    }

    public void setLayoutColor(ViewHolder holder, boolean completed) {
        int color;

        if (completed) {
            color = R.color.main_color_3;
        } else {
            if (holder.getBindingAdapterPosition() % 2 == 0) {
                color = R.color.dark_background;
            } else {
                color = R.color.light_background;
            }
        }

        holder.layout.setBackgroundColor(context.getColor(color));
    }

    @Override
    public int getItemCount() {
        return achievements.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView achievementName, achievementInfo;
        View layout;

        ViewHolder(View view) {
            super(view);
            layout = view;
            achievementName = view.findViewById(R.id.achievement_name);
            achievementInfo = view.findViewById(R.id.achievements_info);
        }
    }
}
