package com.BSLCommunity.CSN_student.Managers;

import android.content.Context;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.BSLCommunity.CSN_student.R;

public class AnimationManager {

    public static void setAnimation(Window window, Context context) {
        Fade fade = new Fade();
        fade.setDuration(context.getResources().getInteger(R.integer.activities_animation_duration));
        fade.setInterpolator(new AccelerateDecelerateInterpolator());
        window.setExitTransition(fade);
        window.setEnterTransition(fade);
    }

}
