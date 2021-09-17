package com.BSLCommunity.CSN_student.Views.decorators;

import android.graphics.drawable.TransitionDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.BSLCommunity.CSN_student.App;
import com.BSLCommunity.CSN_student.R;

public class AnimOnTouchListener implements View.OnTouchListener {
    private View.OnTouchListener listener;

    public AnimOnTouchListener(View.OnTouchListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        TransitionDrawable transitionDrawable = (TransitionDrawable) view.getBackground();

        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            transitionDrawable.startTransition(150);
            view.startAnimation(AnimationUtils.loadAnimation(App.getApp().getApplicationContext(), R.anim.btn_pressed));
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            transitionDrawable.reverseTransition(100);
            view.startAnimation(AnimationUtils.loadAnimation(App.getApp().getApplicationContext(), R.anim.btn_unpressed));
            listener.onTouch(view, motionEvent);
        } else if (motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
            transitionDrawable.reverseTransition(100);
            view.startAnimation(AnimationUtils.loadAnimation(App.getApp().getApplicationContext(), R.anim.btn_unpressed));
        }

        return false;
    }
}
