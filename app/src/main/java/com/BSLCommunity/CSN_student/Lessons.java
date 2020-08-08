package com.BSLCommunity.CSN_student;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Lessons extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    final int MAX_PAIR = 5;
    final int MAX_DAYS = 5;

    TextView[][] schedule = new TextView[MAX_PAIR][MAX_DAYS];
    TextView type_week;
    Spinner group_spin;
    String file_name;
    String group;

    //обьект сохраненого расписание
    Lesson[][][] lesson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lessons_schedule);

        createSpinner();
        getScheduleElements();
        downloadSchedule();
    }

    //создаем спинер выбора группы
    protected void createSpinner() {
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

    //если в спинере была выбрана группа
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        downloadSchedule();
   }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }

    //получение необходимых обьектов
    protected void getScheduleElements() {
        type_week = findViewById(R.id.type_week);

        //получеам id текстовых полей с activity_lessons_schedule и сохраняем их в массиве schedule[][]
        //i - пары, j - дни
        for (int i = 0; i < MAX_DAYS; ++i) {
            for (int j = 0; j < MAX_PAIR; ++j) {
                schedule[i][j] = findViewById(getResources().getIdentifier("text" + (j + 1) + "_" + (i + 2), "id", getApplicationContext().getPackageName()));
            }
        }
    }

    protected void updateSchedule() {
        //выбираем неделю в зависимости от выбранной недели
        int numType = type_week.getText().equals(getResources().getString(R.string.denominator)) ? 0 : 1;


        for (int i = 0; i < MAX_DAYS; ++i) {
            for (int j = 0; j < MAX_PAIR; ++j) {
                try {
                    //парсим предмет по установленому языку в приложении
                    JSONObject subjectJSONObject = new JSONObject(lesson[numType][i][j].subject);
                    String subject = subjectJSONObject.getString(Locale.getDefault().getLanguage());

                    JSONObject typeJSONObject = new JSONObject(lesson[numType][i][j].type);
                    String type = typeJSONObject.getString(Locale.getDefault().getLanguage());

                    schedule[i][j].setText(subject + " " + type + " (" + lesson[numType][i][j].room + ")");
                } catch (Exception e) {
                }
            }
        }
    }

    //меняем тип недели
    public void changeTypeWeek(View v) {
        if (type_week.getText().equals(getResources().getString(R.string.denominator)))
            type_week.setText(getResources().getString(R.string.numerator));
        else
            type_week.setText(getResources().getString(R.string.denominator));
        updateSchedule();
    }

    public void downloadSchedule() {
        //узнаем какая группа выбрана
        group = group_spin.getSelectedItem().toString();

        //обьект запроса
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST,  MainActivity.NEW_MAIN_URL + "getSchedule.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //проверяем, есть ли соеденение или нет
                try {
                    JSONArray JSONObject = new JSONArray(response);

                    //сохраняем расписание в отдельный json файл
                    JSONHelper.create(Lessons.this,group,response);

                    //парсим расписание
                    lesson = new Lesson[JSONObject.length()][100][100];
                    for (int i = 0; i < JSONObject.length(); ++i) {
                        JSONObject dayJSONObject = JSONObject.getJSONObject(i);
                        String day = dayJSONObject.getString("Day");
                        String half = dayJSONObject.getString("Half");
                        String pair = dayJSONObject.getString("Pair");
                        String discipline = dayJSONObject.getString("NameDiscipline");
                        String room = dayJSONObject.getString("Room");
                        String type = dayJSONObject.getString("SubjectType");

                        lesson[Integer.parseInt(half)][Integer.parseInt(day)-1][Integer.parseInt(pair)-1] = new Lesson(discipline,type,Integer.parseInt(room));

                        //устанавливаем расписание
                        updateSchedule();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(Lessons.this, "local schedule", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Lessons.this, R.string.no_connection, Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("NameGroup", group);
                return parameters;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }
}

