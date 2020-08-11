package com.BSLCommunity.CSN_student.Activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.BSLCommunity.CSN_student.Managers.JSONHelper;
import com.BSLCommunity.CSN_student.Objects.Groups;

import com.BSLCommunity.CSN_student.R;
import com.BSLCommunity.CSN_student.Objects.User;
import com.BSLCommunity.CSN_student.R;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import static com.BSLCommunity.CSN_student.Objects.Teachers.getTeachers;

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
    ScheduleList[][][] scheduleList;  //сохраненое расписание
    private boolean isFirst = true; //инициализация первого раза
    String entity; //тип расписания

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

        Groups groups = Groups.getInstance(this);
        User user = User.getInstance();
        int id = 0;
        List<String> groupsAdapter = new ArrayList<>();

        //Выбор расписания в зависимости от пришедшего значения с активити
        if (getIntent().getExtras().getString("typeSchedule").equals("Teachers")){
            entity = "teachers";
            groupsAdapter.add("Downloading");
        }
        else{
            entity = "groups";
            //создаем лист групп
            if (groups.groupsLists.length != 0) {
                //добавляем в массив из класса Groups группы
                for (int j = 0; j < groups.groupsLists.length; ++j) {
                    groupsAdapter.add(groups.groupsLists[j].GroupName);

                    //узнаем ид группы юзера в спинере для дальнейшем установки в кач-ве дефолтного значения
                    if (groups.groupsLists[j].id == user.groupId) id = j;
                }
            } else {
                //в том случае если групп по курсу нету
                groupsAdapter.add("No groups");
            }
        }

        //устанвливаем стандартное значение
        groupSpinner.setSelection(id);

        //устанавливаем спинер
        //устанавливаем спинер выбора групп
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.color_spinner_schedule, groupsAdapter);
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_schedule);
        groupSpinner.setAdapter(dataAdapter);
        groupSpinner.setOnItemSelectedListener(this);
    }

    //если в спинере была выбрана группа
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        downloadSchedule(entity, id);
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
    public void downloadSchedule(String entity, long id) {
        //обьект запроса
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        int groupId = 0;   //переменная id группы
        final String ScheduleFileName = "Schedule_" + id; //сохранения расписания
        String url;

        if(entity == "groups"){
            //в начале ставится группа юзера, а затем в зависимости от id на спинере
            if(isFirst){
                groupId = User.getInstance().groupId;
                isFirst = false;
            }else{
                groupId = Groups.getInstance(this).groupsLists[(int)id].id;
            }
            url = Main.MAIN_URL + String.format("api/" + entity +"/%1$s/schedule", groupId);
        }else{
            if(isFirst){
                getTeachers(this, groupSpinner, R.layout.color_spinner_schedule);
                isFirst = false;
            }
            url = Main.MAIN_URL + String.format("api/" + entity +"/%1$s/schedule", id+1);
        }

        StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //сохраняем расписание в отдельный json файл
                    JSONHelper.create(Schedule.this, ScheduleFileName, response);
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
                    String response = JSONHelper.read(Schedule.this, ScheduleFileName);
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

