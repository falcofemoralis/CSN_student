package com.BSLCommunity.CSN_student.Views;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.BSLCommunity.CSN_student.Managers.AnimationManager;
import com.BSLCommunity.CSN_student.Managers.LocaleHelper;
import com.BSLCommunity.CSN_student.Models.ScheduleList;
import com.BSLCommunity.CSN_student.Presenters.SchedulePresenter;
import com.BSLCommunity.CSN_student.R;
import com.BSLCommunity.CSN_student.ViewInterfaces.ScheduleView;

import java.util.ArrayList;

// Форма расписания предметов у групп\упрепода
public class ScheduleActivity extends BaseActivity implements ScheduleView, AdapterView.OnItemSelectedListener {
    private final int MAX_PAIR = 5; // Ккол-во пар в таблице
    private final int MAX_DAYS = 5; // Кол-во дней в таблице
    private final TextView[][] scheduleTextView = new TextView[MAX_DAYS][MAX_PAIR]; // Элементы таблицы
    private Spinner spinner; // Спиннер выбора элементов
    private TextView weekTypeView; // Тип недели
    private SchedulePresenter schedulePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnimationManager.setAnimation(getWindow(), this);
        setContentView(R.layout.activity_lessons_schedule);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        weekTypeView = findViewById(R.id.activity_lessons_schedule_bt_weekType);

        for (int i = 0; i < MAX_DAYS; ++i)
            for (int j = 0; j < MAX_PAIR; ++j)
                scheduleTextView[i][j] = findViewById(getResources().getIdentifier("text_" + i + "_" + j, "id", getApplicationContext().getPackageName()));

        schedulePresenter = new SchedulePresenter(
                this,
                (SchedulePresenter.EntityTypes) getIntent().getSerializableExtra("EntityTypes"),
                LocaleHelper.getLanguage(getApplicationContext())
        );
        schedulePresenter.initSpinnerData();
        schedulePresenter.initSchedule();
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
     * @param view - вью кнопки
     */
    public void changeWeekType(View view) {
        TransitionManager.beginDelayedTransition((LinearLayout) findViewById(R.id.activity_lessons_schedule_ll_main));

        if (weekTypeView.getText().equals(getResources().getString(R.string.denominator))) {
            weekTypeView.setText(getResources().getString(R.string.numerator));
            schedulePresenter.changeHalf(0);
        } else {
            weekTypeView.setText(getResources().getString(R.string.denominator));
            schedulePresenter.changeHalf(1);
        }
    }

    @Override
    public void setSpinnerData(ArrayList<String> entities, int defaultItem, SchedulePresenter.EntityTypes type) {
        spinner = findViewById(R.id.activity_lessons_schedule_sp_main);
        ArrayAdapter<String> dataAdapter;

        if (type == SchedulePresenter.EntityTypes.GROUPS) {
            spinner.setPrompt(getString(R.string.group_prompt));
            dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_schedule_groups_layout, entities);
        } else {
            spinner.setPrompt(getString(R.string.teachers_prompt));
            dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_schedule_layout, entities);
        }

        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_white);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(defaultItem);
    }

    @Override
    public void setSchedule(ArrayList<ScheduleList> scheduleList, int half, SchedulePresenter.EntityTypes type) {
        clearSchedule();

        for (int i = 0; i < scheduleList.size(); ++i) {
            ScheduleList list = scheduleList.get(i);

            if (list.half == 2 || list.half == half) {
                String content;

                // В зависимости от типа, будет собрана строка для отображения в таблице
                if (type == SchedulePresenter.EntityTypes.GROUPS) {
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
        Toast.makeText(this, R.string.incorrect_data, Toast.LENGTH_SHORT).show();
    }
}

