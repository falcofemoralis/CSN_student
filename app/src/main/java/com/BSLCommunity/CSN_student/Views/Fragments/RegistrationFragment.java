package com.BSLCommunity.CSN_student.Views.Fragments;

import android.content.Context;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.BSLCommunity.CSN_student.Presenters.RegPresenter;
import com.BSLCommunity.CSN_student.R;
import com.BSLCommunity.CSN_student.ViewInterfaces.RegView;
import com.BSLCommunity.CSN_student.Views.OnFragmentInteractionListener;
import com.github.ybq.android.spinkit.style.ThreeBounce;

import java.util.ArrayList;

// Форма регистрации пользователя
public class RegistrationFragment extends Fragment implements RegView, AdapterView.OnItemSelectedListener, View.OnTouchListener {

    String groupName; //выбранный код группы со спиннера
    ProgressBar progressBarGroups, progressBarCourses; //анимация загрузки
    private RegPresenter regPresenter;

    View currentFragment;
    OnFragmentInteractionListener fragmentListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentListener = (OnFragmentInteractionListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        currentFragment = inflater.inflate(R.layout.fragment_registration, container, false);

        progressBarGroups = currentFragment.findViewById(R.id.activity_registration_pb_groups);
        progressBarGroups.setIndeterminateDrawable(new ThreeBounce());
        progressBarCourses = currentFragment.findViewById(R.id.activity_registration_pb_courses);
        progressBarCourses.setIndeterminateDrawable(new ThreeBounce());

        this.createClickableSpan();
        currentFragment.findViewById(R.id.activity_registration_bt_register).setOnTouchListener(this);

        regPresenter = new RegPresenter(this, requireContext());
        this.regPresenter.initSpinnerData();

        return currentFragment;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        TransitionDrawable transitionDrawable = (TransitionDrawable) view.getBackground();

        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
          //  transitionDrawable.startTransition(150);
          //  view.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.btn_pressed));
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            EditText nickName = currentFragment.findViewById(R.id.activity_registration_et_nickname);
            EditText password = currentFragment.findViewById(R.id.activity_registration_et_password);
            EditText repeatPassword = currentFragment.findViewById(R.id.activity_registration_et_passwordRe);

         //   transitionDrawable.reverseTransition(100);
          //  view.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.btn_unpressed));

            changeProgressState(true);
            this.regPresenter.tryRegistration(nickName.getText().toString(), password.getText().toString(), repeatPassword.getText().toString(), this.groupName);
        }
        return false;
    }

    /**
     * Выбор элемента на спинере (используется свитч для определения на каком спинере был выбран элемент)
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.activity_registration_sp_courses) {
            int course = Integer.parseInt(parent.getItemAtPosition(position).toString().split(" ")[1]);
            this.regPresenter.chosenCourse(course);
        } else if (parent.getId() == R.id.activity_registration_sp_groups) {
            groupName = parent.getItemAtPosition(position).toString();
        }
    }

    /**
     * Нужен для реализации интерфейса AdapterView.OnItemSelectedListener
     */
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    /**
     * Переход в активити логина
     */
    public void toLogin() {
        fragmentListener.onFragmentInteraction(this, new LoginFragment(),
                OnFragmentInteractionListener.Action.NEXT_FRAGMENT_NO_BACK_STACK, null, null);
    }

    /**
     * Создание кликабельного текста
     */
    protected void createClickableSpan() {
        TextView text = currentFragment.findViewById(R.id.activity_registration_span2);
        SpannableString ss = new SpannableString(text.getText());

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                toLogin();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(0xFF5EE656);
            }
        };

        ss.setSpan(clickableSpan, 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        text.setText(ss);
        text.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void showToastError(String error) {
        changeProgressState(false);
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
    }

    /**
     * Смена визуальной загрузки
     *
     * @param state - true: загрузка включена
     */
    public void changeProgressState(boolean state) {
        Button registerBtn = currentFragment.findViewById(R.id.activity_registration_bt_register);
        ProgressBar progressBar = currentFragment.findViewById(R.id.activity_login_pb_loading);

        if (state) {
            registerBtn.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            registerBtn.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void openMain() {
        changeProgressState(false);

        fragmentListener.onFragmentInteraction(this, new MainFragment(),
                OnFragmentInteractionListener.Action.NEXT_FRAGMENT_NO_BACK_STACK, null, null);
    }

    @Override
    public void setSpinnersData(ArrayList<String> groupNames, ArrayList<String> courses) {
        int[] idSpins = new int[]{R.id.activity_registration_sp_groups, R.id.activity_registration_sp_courses};
        for (int i = 0; i < courses.size(); ++i) {
            courses.set(i, getString(R.string.course) + " " + courses.get(i));
        }

        ArrayList<String>[] lists = new ArrayList[]{groupNames, courses};

        for (int i = 0; i < idSpins.length; ++i) {
            Spinner spinner = currentFragment.findViewById(idSpins[i]);
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_registration_layout, lists[i]);
            dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_registration);
            spinner.setAdapter(dataAdapter);
            spinner.setOnItemSelectedListener(this);
        }
    }

    @Override
    public void setGroupNamesSpinner(ArrayList<String> groupNames) {
        Spinner spinner = currentFragment.findViewById(R.id.activity_registration_sp_groups);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_registration_layout, groupNames);
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_registration);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void visibilityProgressBar(boolean show) {
        int visibility = show ? ProgressBar.VISIBLE : ProgressBar.GONE;
        progressBarGroups.setVisibility(visibility);
        progressBarCourses.setVisibility(visibility);
    }

}