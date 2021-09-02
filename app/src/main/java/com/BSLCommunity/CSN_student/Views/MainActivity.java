package com.BSLCommunity.CSN_student.Views;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.BSLCommunity.CSN_student.Constants.ActionBarType;
import com.BSLCommunity.CSN_student.Constants.LogType;
import com.BSLCommunity.CSN_student.Managers.LocaleHelper;
import com.BSLCommunity.CSN_student.Managers.LogsManager;
import com.BSLCommunity.CSN_student.Models.UserData;
import com.BSLCommunity.CSN_student.R;
import com.BSLCommunity.CSN_student.ViewInterfaces.GradeCalculatorView;
import com.BSLCommunity.CSN_student.Views.Fragments.GradeCalculatorFragment;
import com.BSLCommunity.CSN_student.Views.Fragments.MainFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnFragmentInteractionListener, OnFragmentActionBarChangeListener {
    private FragmentManager fragmentManager;
    private static Fragment mainFragment;
    private static boolean isFirstLoad = true;

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(LocaleHelper.onAttach(context));
        LocaleHelper.applyOverrideConfiguration(context, getResources().getConfiguration());
    }

    @SuppressLint({"ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isFirstLoad) {
            LocaleHelper.onAttach(this);
            LogsManager.getInstance().updateLogs(LogType.OPENED_APP);
            isFirstLoad = false;
        }
        setContentView(R.layout.main_activity);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#21c5df")));
            actionBar.hide();
        }

        fragmentManager = getSupportFragmentManager();

        if (savedInstanceState == null) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            // Инициализация менеджера смены фрагментов
            mainFragment = new MainFragment();

            // Открытие фрагмента главного меню
            fragmentManager.beginTransaction()
                    .add(R.id.activity_main_ll_container, mainFragment)
                    .commit();
        }
    }

    @Override
    public void onFragmentInteraction(Fragment fragmentSource, Fragment fragmentReceiver, OnFragmentInteractionListener.Action action, Bundle data, String backStackTag) {
        FragmentTransaction fTrans = fragmentManager.beginTransaction();
        if (fragmentReceiver != null)
            fragmentReceiver.setArguments(data);

        int animIn = R.anim.fade_in, animOut = R.anim.fade_out;

        fTrans.setCustomAnimations(animIn, animOut, animIn, animOut);

        switch (action) {
            case NEXT_FRAGMENT_NO_BACK_STACK:
                fTrans.replace(R.id.activity_main_ll_container, fragmentReceiver);
                fTrans.commit();
                break;
            case NEXT_FRAGMENT_HIDE:
                if (mainFragment.isVisible())
                    fTrans.hide(mainFragment);
                else
                    fTrans.hide(fragmentSource);

                fTrans.add(R.id.activity_main_ll_container, fragmentReceiver);
                fTrans.addToBackStack(backStackTag);   // Добавление изменнений в стек
                fTrans.commit();
                break;
            case NEXT_FRAGMENT_REPLACE:
                fTrans.replace(R.id.activity_main_ll_container, fragmentReceiver);
                fTrans.addToBackStack(backStackTag);   // Добавление изменнений в стек
                fTrans.commit();
                break;
            case RETURN_FRAGMENT_BY_TAG:
                fragmentManager.popBackStack(backStackTag, 0);
                break;
            case POP_BACK_STACK:
                fragmentManager.popBackStack();
                break;
        }
    }

    @Override
    public void changeActionBarState(boolean state) {
        if (state) {
            getSupportActionBar().show();
        } else {
            getSupportActionBar().hide();
        }
    }

    @Override
    public void setActionBarColor(int colorRes, final ActionBarType type) {
        Window window = getWindow();
        int colorTo = getColor(colorRes), colorFrom, duration = getResources().getInteger(R.integer.animation_duration);
        String property;

        if (type == ActionBarType.STATUS_BAR) {
            colorFrom = window.getStatusBarColor();
            property = "statusBarColor";
        } else {
            colorFrom = window.getNavigationBarColor();
            property = "navigationBarColor";
        }

        AnimatorSet fade = new AnimatorSet();
        fade.play(ObjectAnimator.ofObject(getWindow(), property, new ArgbEvaluator(), colorFrom, colorTo));
        fade.setDuration(duration);
        fade.start();
    }

    // Отклик на BackPressed во фрагментах.
    public interface onBackPressedListener {
        // Обработка BackPressed во фрагмента. Возврат : true - фрагмент можно закрыть, false - фрагмент должен жить
        // Если onBackPressed() возвращает false, то фрагмент сам должен позаботится о освобождении backStack-а
        boolean onBackPressed();
    }

    @Override
    public void onBackPressed() {
        List<Fragment> fragments = fragmentManager.getFragments();
        for(Fragment fragment : fragments){
            if(fragment.getClass() == GradeCalculatorFragment.class){
                GradeCalculatorFragment gradeCalculatorFragment = (GradeCalculatorFragment) fragment;
                gradeCalculatorFragment.initSubjects();
                gradeCalculatorFragment.initActionBar();
            }
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        UserData.getUserData().setUserActivity();
        super.onPause();
    }
}