package com.BSLCommunity.CSN_student.Views.Fragments;

import android.annotation.SuppressLint;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.BSLCommunity.CSN_student.Presenters.LoginPresenter;
import com.BSLCommunity.CSN_student.R;
import com.BSLCommunity.CSN_student.ViewInterfaces.LoginView;
import com.BSLCommunity.CSN_student.Views.OnFragmentInteractionListener;

// Форма логина для пользователя
public class LoginFragment extends Fragment implements LoginView, View.OnTouchListener {

    LoginPresenter loginPresenter;
    View currentFragment;
    OnFragmentInteractionListener fragmentListener;
    Button loginButton;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentListener = (OnFragmentInteractionListener) context;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        currentFragment = inflater.inflate(R.layout.fragment_login, container, false);

        createClickableSpan();
        loginButton = (Button) currentFragment.findViewById(R.id.activity_login_bt_login);
        loginButton.setOnTouchListener(this);

        this.loginPresenter = new LoginPresenter(this, requireContext());

        return currentFragment;
    }

    //обработчик перехода на форму регистрации
    public void OnClickRegistration() {
        fragmentListener.onFragmentInteraction(this, new RegistrationFragment(),
                OnFragmentInteractionListener.Action.NEXT_FRAGMENT_NO_BACK_STACK, null, null);
    }

    //кнопка перехода в регистрацию
    protected void createClickableSpan() {
        TextView text = currentFragment.findViewById(R.id.activity_login_tv_span2);

        SpannableString ss = new SpannableString(text.getText());

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                OnClickRegistration();
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        TransitionDrawable transitionDrawable = (TransitionDrawable) view.getBackground();

        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
         //   transitionDrawable.startTransition(150);
          //  view.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.btn_pressed));
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
        //    transitionDrawable.reverseTransition(100);
          // view.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.btn_unpressed));

            EditText nickname = (EditText) currentFragment.findViewById(R.id.activity_login_et_nickname);
            EditText password = (EditText) currentFragment.findViewById(R.id.activity_login_et_password);

            changeProgressState(true);
            this.loginPresenter.tryLogin(nickname.getText().toString(), password.getText().toString());
        }
        return false;
    }

    @Override
    public void showToastError(String error) {
        changeProgressState(false);
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void openMain() {
        changeProgressState(false);
        fragmentListener.onFragmentInteraction(this, new MainFragment(),
                OnFragmentInteractionListener.Action.NEXT_FRAGMENT_NO_BACK_STACK, null, null);
    }

    /**
     * Смена визуальной загрузки
     *
     * @param state - true: загрузка включена
     */
    public void changeProgressState(boolean state) {
        ProgressBar progressBar = currentFragment.findViewById(R.id.activity_login_pb_loading);

        if (state) {
            loginButton.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            loginButton.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }
}


