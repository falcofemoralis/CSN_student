package com.BSLCommunity.CSN_student.Views.Fragments;

import android.content.Context;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.BSLCommunity.CSN_student.Constants.ActionBarType;
import com.BSLCommunity.CSN_student.Constants.GrantType;
import com.BSLCommunity.CSN_student.Constants.LogType;
import com.BSLCommunity.CSN_student.Constants.MarkErrorType;
import com.BSLCommunity.CSN_student.Constants.SubjectValue;
import com.BSLCommunity.CSN_student.Managers.LocaleHelper;
import com.BSLCommunity.CSN_student.Managers.LogsManager;
import com.BSLCommunity.CSN_student.Presenters.GradeCalculatorPresenter;
import com.BSLCommunity.CSN_student.R;
import com.BSLCommunity.CSN_student.ViewInterfaces.GradeCalculatorView;
import com.BSLCommunity.CSN_student.Views.OnFragmentActionBarChangeListener;
import com.BSLCommunity.CSN_student.Views.OnFragmentInteractionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;

public class GradeCalculatorFragment extends Fragment implements GradeCalculatorView, View.OnTouchListener {
    private GradeCalculatorPresenter gradeCalculatorPresenter;
    private View currentFragment;
    private TableLayout subjectsContainer;
    private ArrayList<TableRow> subjectsViews;
    private OnFragmentActionBarChangeListener onFragmentActionBarChangeListener;
    private OnFragmentInteractionListener fragmentListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentListener = (OnFragmentInteractionListener) context;
        onFragmentActionBarChangeListener = (OnFragmentActionBarChangeListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        currentFragment = inflater.inflate(R.layout.fragment_gradecalculator, container, false);
        subjectsViews = new ArrayList<>();

        subjectsContainer = currentFragment.findViewById(R.id.gradecalculator_sv_subjects);
        (currentFragment.findViewById(R.id.gradecalculator_bt_calculate)).setOnTouchListener(this);
        gradeCalculatorPresenter = new GradeCalculatorPresenter(this);

        createHintMenu();
        createNoSubjectsHint();
        initSubjects();
        return currentFragment;
    }

    @Override
    public void onResume() {
        initActionBar();
        super.onResume();
    }

    @Override
    public void onPause() {
        onFragmentActionBarChangeListener.setActionBarColor(R.color.background, ActionBarType.STATUS_BAR);
        onFragmentActionBarChangeListener.setActionBarColor(R.color.background, ActionBarType.NAVIGATION_BAR);
        super.onPause();
    }

    public void initSubjects() {
        subjectsViews.clear();
        subjectsContainer.removeAllViews();
        gradeCalculatorPresenter.initSubjects();
    }

    public void initActionBar(){
        onFragmentActionBarChangeListener.setActionBarColor(R.color.dark_blue, ActionBarType.STATUS_BAR);
        onFragmentActionBarChangeListener.setActionBarColor(R.color.dark_red, ActionBarType.NAVIGATION_BAR);
    }

    public void createHintMenu() {
        TextView text = currentFragment.findViewById(R.id.gradecalculator_tv_hint);

        SpannableString ss = new SpannableString(text.getText());

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                LogsManager.getInstance().updateLogs(LogType.OPENED_GRADE_HINT);

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                ViewGroup viewGroup = currentFragment.findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_calculator_hint, viewGroup, false);
                builder.setView(dialogView);

                final AlertDialog alertDialog = builder.create();
                alertDialog.show();

                (dialogView.findViewById(R.id.dialog_calculator_bt_ok)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.hide();
                    }
                });
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
    public void setSubject(String name, SubjectValue type) {
        final TableRow subjectView = (TableRow) getLayoutInflater().inflate(R.layout.inflate_calculator_subject, subjectsContainer, false);
        final TextView subjectNameTV = subjectView.findViewById(R.id.inflate_calculator_tv_name);
        final TextView subjectMarkLetter = subjectView.findViewById(R.id.inflate_calculator_tv_mark_type);

        try {
            name = new JSONObject(name).getString(LocaleHelper.getLanguage(getContext()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (name.length() > 38)
            subjectNameTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, getContext().getResources().getDimension(R.dimen._3ssp));
        subjectNameTV.setText(name);

        ((EditText) subjectView.findViewById(R.id.inflate_calculator_et_mark)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    subjectMarkLetter.setText("-");
                } else {
                    try {
                        subjectMarkLetter.setText(gradeCalculatorPresenter.convert100ValueToLetter(Integer.parseInt(s.toString())));
                    } catch (Exception e) {
                        subjectMarkLetter.setText("-");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        subjectsViews.add(subjectView);
        subjectsContainer.addView(subjectView);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        TransitionDrawable transitionDrawable = (TransitionDrawable) view.getBackground();

        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            transitionDrawable.startTransition(150);
            view.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.btn_pressed));
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
            ArrayList<String> marks = new ArrayList<>();

            for (TableRow subjectsView : subjectsViews) {
                marks.add(((EditText) subjectsView.findViewById(R.id.inflate_calculator_et_mark)).getText().toString());
            }

            gradeCalculatorPresenter.calculateResult(marks);

            LogsManager.getInstance().updateLogs(LogType.CALCULATED_GRADE);
            transitionDrawable.reverseTransition(100);
            view.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.btn_unpressed));
        }
        return true;
    }

    public void showResult(float result100, float result5) {
        final NumberFormat numFormat = NumberFormat.getNumberInstance();
        numFormat.setMaximumFractionDigits(3);
        ((TextView) currentFragment.findViewById(R.id.gradecalculator_tv_result_100)).setText(numFormat.format(result100));
        ((TextView) currentFragment.findViewById(R.id.gradecalculator_tv_result_5)).setText(numFormat.format(result5));
    }

    public void showMsg(MarkErrorType type) {
        String text = "";
        switch (type) {
            case MORE100:
                text = getString(R.string.markismore100);
                break;
            case EXAM_FAILED:
                text = getString(R.string.exam_failed);
                break;
        }
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }

    public void setGrant(GrantType type) {
        String text = "";
        switch (type) {
            case HIGH_GRANT:
                text = getString(R.string.high_grant);
                break;
            case REGULAR_GRANT:
                text = getString(R.string.regular_grant);
                break;
            case NO_GRANT:
                text = getString(R.string.no_grant);
                break;
        }
        ((TextView) currentFragment.findViewById(R.id.gradecalculator_tv_grant)).setText(text);
    }

    public void createNoSubjectsHint() {
        final Fragment fragment = this;
        TextView text = currentFragment.findViewById(R.id.gradecalculator_tv_no_subjects_menu);
        SpannableString ss = new SpannableString(text.getText());

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                LogsManager.getInstance().updateLogs(LogType.OPENED_SUBJECTS);
                fragmentListener.onFragmentInteraction(fragment, new SubjectListFragment(), OnFragmentInteractionListener.Action.NEXT_FRAGMENT_HIDE, null, "subjects_hint");
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
    public void showSubjects() {
        currentFragment.findViewById(R.id.gradecalculator_ll_no_subjects).setVisibility(View.GONE);
        subjectsContainer.setVisibility(View.VISIBLE);
        currentFragment.findViewById(R.id.gradecalculator_tv_hint).setVisibility(View.VISIBLE);
    }
}
