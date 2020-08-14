package com.BSLCommunity.CSN_student.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.BSLCommunity.CSN_student.Objects.Groups;
import com.BSLCommunity.CSN_student.Objects.LocalData;
import com.BSLCommunity.CSN_student.Objects.User;
import com.BSLCommunity.CSN_student.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

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
    String entity; //тип расписания

    int[] idGroups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lessons_schedule);

        // Загружаем информацию о группах и после проверяем актуальность данных
        Groups.init(getApplicationContext(), User.getInstance().course, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                LocalData.checkUpdate(getApplicationContext(), LocalData.TypeData.groups);
                createGroupSpinner();
                getScheduleElements();
                return null;
            }
        });
    }

    //создание спиннера групп
    protected void createGroupSpinner() {
        groupSpinner = findViewById(R.id.group_spin);

        User user = User.getInstance();
        int id = 0;
        List<String> adapter = new ArrayList<>();

        //Выбор расписания в зависимости от пришедшего значения с активити
        if (getIntent().getExtras().getString("typeSchedule").equals("Teachers")){
            entity = "teachers";
            adapter.add("Downloading");
        }
        else{
            entity = "groups";
            //создаем лист групп
            if (Groups.groupsLists.size() != 0) {
                //добавляем в массив из класса Groups группы
                idGroups = new int[Groups.groupsLists.size()];
                for (int j = 0; j < Groups.groupsLists.size(); ++j) {
                    adapter.add(Groups.groupsLists.get(j).GroupName);
                    idGroups[j] = Groups.groupsLists.get(j).id;
                    //узнаем ид группы юзера в спинере для дальнейшем установки в кач-ве дефолтного значения
                    if (Groups.groupsLists.get(j).id == user.groupId)
                        id = j;
                }
            } else {
                //в том случае если групп по курсу нету
                adapter.add("No groups");
            }
        }

        //устанвливаем стандартное значение
        groupSpinner.setSelection(id);

        //устанавливаем спинер
        //устанавливаем спинер выбора групп
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.color_spinner_schedule, adapter);
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_schedule);
        groupSpinner.setAdapter(dataAdapter);
        groupSpinner.setOnItemSelectedListener(this);
    }

    //если в спинере была выбрана группа
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        try {
            updateSchedule(idGroups[(int)id]);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    //обновляем данные json строки в массив расписания
    protected void updateSchedule(int id) throws JSONException {

        Groups.GroupsList group =  Groups.findById(id);
        //Обновляем расписание
        scheduleList = new ScheduleList[2][100][100];
        for (int i = 0; i <  group.scheduleList.size(); ++i) {
            Groups.GroupsList.ScheduleList groupScheduleList = group.scheduleList.get(i);
            scheduleList[groupScheduleList.half][groupScheduleList.day][groupScheduleList.pair] = new ScheduleList(groupScheduleList.subject, groupScheduleList.type, groupScheduleList.room);
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

