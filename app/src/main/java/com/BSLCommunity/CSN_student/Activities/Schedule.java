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

public class Schedule extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    //кол-во дней и пар в активити
    final int MAX_PAIR = 5;
    final int MAX_DAYS = 5;

    TextView[][] scheduleTextView = new TextView[MAX_DAYS][MAX_PAIR];
    TextView type_week;
    Spinner group_spin;
    String group;

    //обьект сохраненого расписание
    ScheduleList[][][] scheduleList;

    long groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lessons_schedule);

        createSpinner();
        getScheduleElements();
    }

    //создаем спинер выбора группы
    protected void createSpinner() {
        group_spin = findViewById(R.id.group_spin);

        //создаем лист групп
        List<String> groups = new ArrayList<String>();
        for (int i = 0; i < User.GROUPS.length; ++i)
            groups.add(User.GROUPS[i].GroupName);

        //устанавливаем спинер
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.color_spinner_schedule, groups);
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_schedule);
        group_spin.setAdapter(dataAdapter);
        group_spin.setOnItemSelectedListener(this);
    }

    //если в спинере была выбрана группа
    //запускается после onCreate
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        groupId = id;
        downloadSchedule();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    //получение необходимых обьектов
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

    //устанавливаем расписание
    protected void setSchedule() {
        //выбираем неделю в зависимости от выбранной недели
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
                    scheduleTextView[i][j].setText("");
                }
            }
        }
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

    //скачиваем расписание с сервера
    public void downloadSchedule() {
        //узнаем какая группа выбрана
        group = group_spin.getSelectedItem().toString();

        //обьект запроса
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, Main.MAIN_URL + "getSchedule.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //сохраняем расписание в отдельный json файл
                    JSONHelper.create(Schedule.this, group, response);
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
                    String response = JSONHelper.read(Schedule.this, group);
                    updateSchedule(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("groupId", String.valueOf(User.GROUPS[(int) groupId].Code_Group));
                return parameters;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    //меняем тип недели
    public void changeTypeWeek(View v) {
        if (type_week.getText().equals(getResources().getString(R.string.denominator)))
            type_week.setText(getResources().getString(R.string.numerator));
        else
            type_week.setText(getResources().getString(R.string.denominator));
        setSchedule();
    }
}

