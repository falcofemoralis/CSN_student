package com.example.ksm_2_course;

import androidx.appcompat.app.AppCompatActivity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;

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
        int numType = type_week.getText().equals(getResources().getString(R.string.denominator)) ? 0 : 1;

        file_name = (group_spin.getSelectedItem().toString()).equals("КНТ-518") ? "knt518.json" : "knt528.json";

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

                if (item.getSubject().equals("-")) {
                    schedule[j][i].setText("");
                    continue;
                }
                schedule[j][i].setText(item.getSubject() + " " + item.getType() + " (" + item.getRoom() + ")");
            }
        }
    }

    protected void setSchedule()
    {
        int id;
        type_week = findViewById(R.id.type_week);

        for (int i = 0; i < MAX_PAIR; ++i) {
            for (int j = 0; j < MAX_DAYS; ++j) {
                id = getResources().getIdentifier("text" + (i + 1) +  "_" + (j + 2), "id", getApplicationContext().getPackageName());
                schedule[i][j] = findViewById(id);
            }
        }
        updateSchedule();
    }

    public void changeTypeWeek(View v)
    {
        if (type_week.getText().equals(getResources().getString(R.string.denominator)))
            type_week.setText(getResources().getString(R.string.numerator));
        else
            type_week.setText(getResources().getString(R.string.denominator));
        updateSchedule();
    }

    protected  void createSpinner()
    {
        group_spin = findViewById(R.id.group_spin);

        ArrayList<String> spinnerArray = new ArrayList<String>();

        for (int i = 0; i < MainActivity.GROUPS.length; ++i)
            spinnerArray.add(MainActivity.GROUPS[i].NameGroup);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, R.layout.color_spinner_schedule,spinnerArray);

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
