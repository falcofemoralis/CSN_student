package com.BSLCommunity.CSN_student.Views.Fragments;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.BSLCommunity.CSN_student.Constants.WorkType;
import com.BSLCommunity.CSN_student.Managers.LocaleHelper;
import com.BSLCommunity.CSN_student.Models.EditableSubject;
import com.BSLCommunity.CSN_student.Presenters.FullStatPresenter;
import com.BSLCommunity.CSN_student.R;
import com.BSLCommunity.CSN_student.ViewInterfaces.FullStatView;
import com.BSLCommunity.CSN_student.Views.OnFragmentInteractionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class FullStatFragment extends Fragment implements FullStatView {
    TableLayout worksTL;
    final int TYPES_WORKS_COUNT = WorkType.values().length;

    String[] subjectValues;
    String[] workStatuses;
    final int[] wordStatusColors = {
            R.color.not_passed,
            R.color.in_process,
            R.color.done_without_report,
            R.color.done_with_report,
            R.color.waiting_acceptation,
            R.color.passed_without_report,
            R.color.passed_with_report };

    FullStatPresenter fullStatPresenter;

    View currentFragment;
    OnFragmentInteractionListener fragmentListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentListener = (OnFragmentInteractionListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        currentFragment = inflater.inflate(R.layout.fragment_full_statistic, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        worksTL = currentFragment.findViewById(R.id.activity_subject_info_full_tl_works);

        subjectValues = getResources().getStringArray(R.array.subject_values);
        workStatuses = getResources().getStringArray(R.array.work_statuses);

        fullStatPresenter = new FullStatPresenter(this);
        return currentFragment;
    }


    @Override
    public void addSubjectRow(String subjectName, int idSubjectValue, HashMap<WorkType, ArrayList<EditableSubject.Work>> allWorks, int[] maxWorks) {
        TableRow tableRow = new TableRow(getContext());

        // Добавление ценности предмета
        TextView subjectValueTv = inflateTextView(R.layout.inflate_statistic_view);
        subjectValueTv.setText(subjectValues[idSubjectValue]);
        subjectValueTv.setTextColor(getActivity().getColor(R.color.white));
        tableRow.addView(subjectValueTv);

        // Добавление названия дисциплины
        TextView subjectNameTv = inflateTextView(R.layout.inflate_statistic_view);
        try {
            JSONObject jsonSubjectName = new JSONObject(subjectName);
            subjectNameTv.setText(jsonSubjectName.getString(LocaleHelper.getLanguage(getContext())));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        subjectNameTv.setTextColor(getActivity().getColor(R.color.white));
        subjectNameTv.setGravity(Gravity.START);
        tableRow.addView(subjectNameTv);

        // Добавление работы
        for (int i = 0; i < TYPES_WORKS_COUNT; ++i) {
            ArrayList<EditableSubject.Work> works = allWorks.get(WorkType.values()[i]);

            for (int j = 0; j < maxWorks[i]; ++j) {
                TextView workTv = inflateTextView(R.layout.inflate_statistic_view);
                if (j < works.size()) {
                    int idWorkStatus = works.get(j).workStatus.ordinal();

                    workTv.setText(workStatuses[idWorkStatus]);
                    workTv.setTextColor(getActivity().getColor(R.color.white));
                    Drawable drawable = getActivity().getDrawable(R.drawable.inflate_drawable_statistic_view);
                    drawable.mutate();
                    drawable.setTint(getActivity().getColor(wordStatusColors[idWorkStatus]));
                    workTv.setBackground(drawable);
                }

                tableRow.addView(workTv);
            }
        }

        worksTL.addView(tableRow);
    }

    @Override
    public void addWorksHeaders(int[] maxWorks) {
        TableRow worksHeadersRow = currentFragment.findViewById(R.id.activity_subject_info_full_tr_works_numbers);

        // Заголовок ценности работ
        TextView valueHeader =  inflateTextView(R.layout.inflate_statistic_view_header);
        valueHeader.setText(R.string.value);
        worksHeadersRow.addView(valueHeader);

        // Заголовок предметов
        TextView subjectHeader = inflateTextView(R.layout.inflate_statistic_view_header);
        subjectHeader.setText(R.string.subject);
        worksHeadersRow.addView(subjectHeader);

        // Заголовки работ
        int [] workHeader = {R.string.lab, R.string.ihw, R.string.other};
        for (int i=0;i<TYPES_WORKS_COUNT;++i) {
            for (int j = 0; j < maxWorks[i]; ++j) {
                TextView textView = inflateTextView(R.layout.inflate_statistic_view_header);
                textView.setText(String.format(Locale.getDefault(), "%s %d", getString(workHeader[i]), j + 1));
                textView.setTextColor(getActivity().getColor(R.color.white));
                worksHeadersRow.addView(textView);
            }
        }
    }

    /**
     * Загрузка TextView из шаблонов (inflate) по id
     * @param id - id лаяута который загружается из ресурсов
     * @return елемент TextView
     */
    private TextView inflateTextView(int id){
        return (TextView) LayoutInflater.from(getContext()).inflate(id, null);
    }
}
