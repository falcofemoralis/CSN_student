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
import com.BSLCommunity.CSN_student.Objects.Teachers;
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
    Spinner spinner; //спинер выбора группы
    ScheduleList[][][] scheduleList;  //сохраненое расписание
    String entity; //тип расписания

    int[] idElements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lessons_schedule);

        if (getIntent().getExtras().getString("typeSchedule").equals("Teachers")){
            entity = "teachers";
            Teachers.init(getApplicationContext(), new Callable<Void>() {
                @Override
                public Void call(){
                    LocalData.checkUpdate(getApplicationContext(), LocalData.TypeData.teachers);
                    createSpinner();
                    return null;
                }
            });
        }else{
            entity = "groups";
            // Загружаем информацию о группах и после проверяем актуальность данных
            Groups.init(getApplicationContext(), User.getInstance().course, new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    LocalData.checkUpdate(getApplicationContext(), LocalData.TypeData.groups);
                    createSpinner();
                    return null;
                }
            });
        }
        getScheduleElements();
    }

    //создание спиннера групп
    protected void createSpinner() {
        spinner = findViewById(R.id.activity_lessons_schedule_sp_main);

        User user = User.getInstance();
        int id = 0;
        List<String> listAdapter = new ArrayList<>();

        if (entity.equals("teachers")) {

            //создаем лист групп
            if (Teachers.teacherLists.size() != 0) {
                //добавляем в массив из класса Groups группы
                idElements = new int[Teachers.teacherLists.size()];
                for (int j = 0; j < Teachers.teacherLists.size(); ++j) {
                    listAdapter.add(Teachers.teacherLists.get(j).FIO);
                    idElements[j] = Teachers.teacherLists.get(j).id;
                }
            } else {
                //в том случае если групп по курсу нету
                listAdapter.add("No teachers");
            }

            //устанавливаем спинер
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_schedule_layout, listAdapter);
            dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_white);
            spinner.setAdapter(dataAdapter);
            spinner.setSelection(0);
        }
        else{
            //создаем лист групп
            if (Groups.groupsLists.size() != 0) {
                //добавляем в массив из класса Groups группы
                idElements = new int[Groups.groupsLists.size()];
                for (int j = 0; j < Groups.groupsLists.size(); ++j) {
                    listAdapter.add(Groups.groupsLists.get(j).GroupName);
                    idElements[j] = Groups.groupsLists.get(j).id;
                }
            } else {
                //в том случае если групп по курсу нету
                listAdapter.add("No groups");
            }

            //устанавливаем спинер
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_schedule_layout, listAdapter);
            dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_white);
            spinner.setAdapter(dataAdapter);

            for (int i = 0; i < Groups.groupsLists.size(); ++i)
                if (user.groupId == Groups.groupsLists.get(i).id)
                    spinner.setSelection(i);
        }
        spinner.setOnItemSelectedListener(this);
    }

    //если в спинере была выбрана группа
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            try {
                updateSchedule(idElements[(int)id]);
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
        type_week = findViewById(R.id.activity_lessons_schedule_bt_weekType);

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

        if (entity.equals("teachers")) {
            Teachers.TeacherList teacher = Teachers.findById(id);
            //Обновляем расписание
            scheduleList = new ScheduleList[2][100][100];
            for (int i = 0; i < teacher.scheduleList.size(); ++i) {
                Teachers.TeacherList.ScheduleList teacherScheduleList = teacher.scheduleList.get(i);
                scheduleList[teacherScheduleList.half][teacherScheduleList.day][teacherScheduleList.pair] = new ScheduleList(teacherScheduleList.subject, teacherScheduleList.type, teacherScheduleList.room);
            }
        } else {
            Groups.GroupsList group = Groups.findById(id);
            //Обновляем расписание
            scheduleList = new ScheduleList[2][100][100];
            for (int i = 0; i < group.scheduleList.size(); ++i) {
                Groups.GroupsList.ScheduleList groupScheduleList = group.scheduleList.get(i);
                scheduleList[groupScheduleList.half][groupScheduleList.day][groupScheduleList.pair] = new ScheduleList(groupScheduleList.subject, groupScheduleList.type, groupScheduleList.room);
            }
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

