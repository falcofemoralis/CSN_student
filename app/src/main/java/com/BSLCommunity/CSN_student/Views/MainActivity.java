package com.BSLCommunity.CSN_student.Views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.BSLCommunity.CSN_student.Managers.LocaleHelper;
import com.BSLCommunity.CSN_student.R;
import com.BSLCommunity.CSN_student.Views.Fragments.MainFragment;

public class MainActivity extends AppCompatActivity implements OnFragmentInteractionListener {

    private FragmentManager fragmentManager;
    private static Fragment  mainFragment;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

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
        FragmentTransaction fTrans = fragmentManager.beginTransaction();//.setCustomAnimations(R.anim.flip_fragment_in, R.anim.flip_fragment_out,  R.anim.flip_fragment_in, R.anim.flip_fragment_out);
        if (fragmentReceiver != null)
            fragmentReceiver.setArguments(data);

        switch (action)
        {
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

    // Отклик на BackPressed во фрагментах.
    public interface onBackPressedListener {
        // Обработка BackPressed во фрагмента. Возврат : true - фрагмент можно закрыть, false - фрагмент должен жить
        // Если onBackPressed() возвращает false, то фрагмент сам должен позаботится о освобождении backStack-а
        boolean onBackPressed();
    }
}