package com.BSLCommunity.CSN_student.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.BSLCommunity.CSN_student.Managers.JSONHelper;
import com.BSLCommunity.CSN_student.R;
import com.BSLCommunity.CSN_student.Objects.User;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import static com.BSLCommunity.CSN_student.Objects.User.getGroups;

/*
 * Класс для сериализации
 *
 * subject - предмет (строка JSON)
 * type - тип предмета (строка JSON)
 * room - номер аудитории
 * */
class ScheduleList {
    public String subject;
    public String type;
    public int room;

    public ScheduleList(String subject, String type, int room) {
        this.room = room;
        this.subject = subject;
        this.type = type;
    }
}

//форма расписание предметов группы
public class Schedule extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    final int MAX_PAIR = 5; //кол-во пар в активити
    final int MAX_DAYS = 5; //кол-во дней в активити

    TextView[][] scheduleTextView = new TextView[MAX_DAYS][MAX_PAIR]; //массив из элементов TextView в активити
    TextView type_week; //тип недели
    Spinner groupSpinner; //спинер выбора группы
    long groupId; // выбранный код группы
    ScheduleList[][][] scheduleList;  //сохраненое расписание

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lessons_schedule);
        createGroupSpinner();
        getScheduleElements();
    }

    //создание спиннера групп
    protected void createGroupSpinner() {
        groupSpinner = findViewById(R.id.group_spin);
        getGroups(this, groupSpinner, 3,R.layout.color_spinner_schedule); //в дальнейшем заменить на User.getInstance().course

        //устанавливаем спинер
        groupSpinner.setOnItemSelectedListener(this);
    }

    //если в спинере была выбрана группа
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //+1 т.к спиннер хранит группы от 0, а в базе от 1
        groupId = id+1; //сохраняем выбранный код группы
        downloadSchedule();
    }

    //нужен для реализации интерфейса AdapterView.OnItemSelectedListener
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    //меняем тип недели
    public void changeTypeWeek(View v) {
        if (type_week.getText().equals(getResources().getString(R.string.denominator)))
            type_week.setText(getResources().getString(R.string.numerator));
        else
            type_week.setText(getResources().getString(R.string.denominator));
        setSchedule();
    }

    //получение необходимых полей с активити расписание
    protected void getScheduleElements() {
        type_week = findViewById(R.id.type_week);

        //получеам id текстовых полей с activity_lessons_schedule и сохраняем их в массиве schedule[][]
        //i - пары, j - дни
        for (int i = 0; i < MAX_DAYS; ++i) {
            for (int j = 0; j < MAX_PAIR; ++j) {
                scheduleTextView[i][j] = findViewById(getResources().getIdentifier("text" + (j + 1) + "_" + (i + 2), "id", getApplicationContext().getPackageName()));
            }
        }
    }

    //скачиваем расписание с сервера
    public void downloadSchedule() {
        //обьект запроса
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        System.out.println(groupId);
        String url = Main.MAIN_URL + String.format("api/groups/%1$s/schedule", groupId);

        StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //сохраняем расписание в отдельный json файл
                    JSONHelper.create(Schedule.this, String.valueOf(groupId), response);
                    updateSchedule(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                    setSchedule();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Schedule.this, "local schedule", Toast.LENGTH_SHORT).show();
                try {
                    //загружаем расписание из отдельного json файла
                    String response = JSONHelper.read(Schedule.this, String.valueOf(groupId));
                    updateSchedule(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    //обновляем данные json строки в массив расписания
    protected void updateSchedule(String response) throws JSONException {
        JSONArray JSONObject = new JSONArray(response);

        //парсим расписание
        scheduleList = new ScheduleList[2][100][100];
        for (int i = 0; i < JSONObject.length(); ++i) {
            JSONObject dayJSONObject = JSONObject.getJSONObject(i);
            String day = dayJSONObject.getString("Day");
            String half = dayJSONObject.getString("Half");
            String pair = dayJSONObject.getString("Pair");
            String discipline = dayJSONObject.getString("NameDiscipline");
            String room = dayJSONObject.getString("Room");
            String type = dayJSONObject.getString("SubjectType");

            scheduleList[Integer.parseInt(half)][Integer.parseInt(day) - 1][Integer.parseInt(pair) - 1] = new ScheduleList(discipline, type, Integer.parseInt(room));
        }
        setSchedule();
    }

    //устанавливаем расписание
    protected void setSchedule() {
        //получаем неделю в зависимости от выбранной недели
        int numType = type_week.getText().equals(getResources().getString(R.string.denominator)) ? 1 : 0;

        for (int i = 0; i < MAX_DAYS; ++i) {
            for (int j = 0; j < MAX_PAIR; ++j) {
                try {
                    //парсим предмет по установленому языку в приложении
                    JSONObject subjectJSONObject = new JSONObject(scheduleList[numType][i][j].subject);
                    String subject = subjectJSONObject.getString(Locale.getDefault().getLanguage());

                    JSONObject typeJSONObject = new JSONObject(scheduleList[numType][i][j].type);
                    String type = typeJSONObject.getString(Locale.getDefault().getLanguage());

                    scheduleTextView[i][j].setText(subject + " " + type + " (" + scheduleList[numType][i][j].room + ")");
                } catch (Exception e) {
                    //если поле пустое
                    scheduleTextView[i][j].setText("");
                }
            }
        }
    }
}

