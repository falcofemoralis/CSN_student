package com.example.ksm_2_course;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class Lessons extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    final int MAX_PAIR = 5;
    final int MAX_DAYS = 5;

    Lesson[] lessons = new Lesson[2];
    TextView[][] schedule = new TextView[MAX_PAIR][MAX_DAYS];
    TextView type_week;
    Spinner group_spin;
    String  file_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lessons_schedule);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        createSpinner();
        setSchedule();
        updateSchedule();
    }

    protected void updateSchedule()
    {
        int numType = type_week.getText().equals("Знаменник") ? 0 : 1;
        String cell;

        if((group_spin.getSelectedItem().toString()).equals("КНТ-518"))
            file_name = "knt518.json";
        else
            file_name = "knt528.json";

        Gson g = new Gson();
        try {
            lessons = g.fromJson(JSONHelper.loadJSONFromAsset(this, file_name), Lesson[].class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0, size = lessons[numType].types.size() / 4; i < size; ++i)
        {
            for (int j = 0; j < 4; ++j)
            {
                ItemType item = lessons[numType].types.get(i * 4 + j);

                if (item.getSubject().equals("-"))
                {
                    schedule[j][i].setText("");
                    continue;
                }
                cell = item.getSubject() + " " + item.getType() + " (" + item.getRoom() + ")";
                schedule[j][i].setText(cell);
            }
        }
    }

    protected void setSchedule()
    {
        String stringId, table;
        int id;
        type_week = findViewById(R.id.type_week);

        for (int i = 0; i < MAX_PAIR; ++i) {
            table = "text" + (i + 1) +  "_";

            for (int j = 0; j < MAX_DAYS; ++j) {
                stringId = table + (j + 2);
                id = getResources().getIdentifier(stringId, "id", getApplicationContext().getPackageName());
                schedule[i][j] = findViewById(id);
            }
        }
        updateSchedule();
    }

    public void changeTypeWeek(View v)
    {
        if (type_week.getText().equals("Знаменник"))
            type_week.setText("Чисельник");
        else
            type_week.setText("Знаменник");
        updateSchedule();
    }

    protected  void createSpinner()
    {
        group_spin = findViewById(R.id.group_spin);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(
                this,
                R.array.group_values,
                R.layout.color_spinner_schedule
        );

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_schedule);
        group_spin.setAdapter(adapter);
        group_spin.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        updateSchedule();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}



