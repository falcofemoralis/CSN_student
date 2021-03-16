package com.BSLCommunity.CSN_student.Views.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.BSLCommunity.CSN_student.Constants.ScheduleType;
import com.BSLCommunity.CSN_student.Managers.LocaleHelper;
import com.BSLCommunity.CSN_student.Models.ScheduleList;
import com.BSLCommunity.CSN_student.Presenters.SchedulePresenter;
import com.BSLCommunity.CSN_student.R;
import com.BSLCommunity.CSN_student.ViewInterfaces.ScheduleView;
import com.BSLCommunity.CSN_student.Views.OnFragmentInteractionListener;

import java.util.ArrayList;


//форма расписание предметов группы
public class ScheduleFragment extends Fragment implements AdapterView.OnItemSelectedListener, ScheduleView, View.OnClickListener {
    private final int MAX_PAIR = 5; // Ккол-во пар в таблице
    private final int MAX_DAYS = 5; // Кол-во дней в таблице
    private final TextView[][] scheduleTextView = new TextView[MAX_DAYS][MAX_PAIR]; // Элементы таблицы
    private Spinner spinner; // Спиннер выбора элементов
    private TextView weekTypeView; // Тип недели
    private SchedulePresenter schedulePresenter;

    View currentFragment;
    OnFragmentInteractionListener fragmentListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentListener = (OnFragmentInteractionListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        currentFragment = inflater.inflate(R.layout.fragment_lessons_schedule, container, false);

        weekTypeView = currentFragment.findViewById(R.id.activity_lessons_schedule_bt_weekType);
        weekTypeView.setOnClickListener(this);

        for (int i = 0; i < MAX_DAYS; ++i)
            for (int j = 0; j < MAX_PAIR; ++j)
                scheduleTextView[i][j] = currentFragment.findViewById(getResources().
                        getIdentifier("text_" + i + "_" + j, "id", getContext().getPackageName()));

        schedulePresenter = new SchedulePresenter(
                this,
                ScheduleType.values()[getArguments().getInt("ScheduleType")],
                LocaleHelper.getLanguage(getContext())
        );
        schedulePresenter.initSpinnerData();
        schedulePresenter.initSchedule();

        return currentFragment;
    }

    /**
     * Выбор элемента на спинере (используется взятие элемента по его позиции из спинера)
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        schedulePresenter.changeItem((String) spinner.getItemAtPosition(position));
    }

    /**
     * Нужен для реализации интерфейса AdapterView.OnItemSelectedListener
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    /**
     * Обработчик нажатия на кнопку типа недели
     *
     * @param v - вью кнопки
     */
    @Override
    public void onClick(View v) {
        TransitionManager.beginDelayedTransition((LinearLayout) currentFragment.findViewById(R.id.activity_lessons_schedule_ll_main));

        if (weekTypeView.getText().equals(getResources().getString(R.string.denominator))) {
            weekTypeView.setText(getResources().getString(R.string.numerator));
            schedulePresenter.changeHalf(0);
        } else {
            weekTypeView.setText(getResources().getString(R.string.denominator));
            schedulePresenter.changeHalf(1);
        }
    }

    @Override
    public void setSpinnerData(ArrayList<String> entities, int defaultItem, ScheduleType type) {
        spinner = currentFragment.findViewById(R.id.activity_lessons_schedule_sp_main);
        ArrayAdapter<String> dataAdapter;

        if (type == ScheduleType.GROUPS) {
            spinner.setPrompt(getString(R.string.group_prompt));
            dataAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_schedule_groups_layout, entities);
        } else {
            spinner.setPrompt(getString(R.string.teachers_prompt));
            dataAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_schedule_layout, entities);
        }

        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_white);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(defaultItem);
    }

    @Override
    public void setSchedule(ArrayList<ScheduleList> scheduleList, int half, ScheduleType type) {
        clearSchedule();

        for (int i = 0; i < scheduleList.size(); ++i) {
            ScheduleList list = scheduleList.get(i);

            if (list.half == 2 || list.half == half) {
                String content;

                // В зависимости от типа, будет собрана строка для отображения в таблице
                if (type == ScheduleType.GROUPS) {
                    content = list.subject + " " + list.type + " (" + list.room + ")";
                } else {
                    StringBuilder groups = new StringBuilder();
                    for (String group : list.groups)
                        groups.append(group).append(" ");

                    content = list.subject + " " + list.type + " (" + list.room + ")\n" + groups;
                }
                scheduleTextView[list.day - 1][list.pair - 1].setText(content);
            }
        }
    }

    @Override
    public void clearSchedule() {
        for (int i = 0; i < MAX_DAYS; ++i)
            for (int j = 0; j < MAX_PAIR; ++j)
                scheduleTextView[i][j].setText("");
    }

    @Override
    public void showToastError() {
        Toast.makeText(getContext(), R.string.incorrect_data, Toast.LENGTH_SHORT).show();
    }
}

