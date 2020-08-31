package com.BSLCommunity.CSN_student.Managers;

import android.transition.Fade;
import android.transition.PathMotion;
import android.transition.Slide;
import android.transition.Visibility;
import android.view.Gravity;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;

public class AnimationManager {

    public static void setAnimation(Window window, int gravity) {
        Fade fade = new Fade();
        fade.setDuration(300);
        fade.setInterpolator(new AccelerateDecelerateInterpolator());
        window.setExitTransition(fade);
        window.setEnterTransition(fade);
    }
}
