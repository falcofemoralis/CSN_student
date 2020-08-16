package com.BSLCommunity.CSN_student.Activities.Schedule;

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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;


//форма расписание предметов группы
public class Schedule extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    final int MAX_PAIR = 5; //кол-во пар в активити
    final int MAX_DAYS = 5; //кол-во дней в активити

    TextView[][] scheduleTextView = new TextView[MAX_DAYS][MAX_PAIR]; //массив из элементов TextView в активити
    TextView type_week; //тип недели
    Spinner spinner; //спинер выбора группы
    ScheduleList[][][] scheduleList;  //сохраненое расписание
    String entity; //тип расписания

    int[] idElements; // id сущностей в спиннере (в порядке расположения их в спиннере)
    int selectedItemId; // ID выбранного элемента в спиннере


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
        }
        else{
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

        User user = User.getInstance(); // Данные пользователя
        int selectFirst = 0; // Для выбора расписания которое будет показано при загрузке самого окна
        List<String> listAdapter = new ArrayList<>(); // Список строк в спиннере

        if (entity.equals("teachers")) {

            //создаем лист групп
            if (Teachers.teacherLists.size() != 0) {
                //добавляем в массив из класса Groups группы
                idElements = new int[Teachers.teacherLists.size()];
                for (int j = 0; j < Teachers.teacherLists.size(); ++j) {
                    try {
                        JSONObject FIOJson = new JSONObject(Teachers.teacherLists.get(j).FIO);
                        listAdapter.add(FIOJson.getString(Locale.getDefault().getLanguage()));
                    }
                    catch (Exception e) {}
                    idElements[j] = Teachers.teacherLists.get(j).id;
                }
                // Выбор первого учителя не имеет смысла
                selectFirst = 0;
            } else {
                //в том случае если групп по курсу нету
                listAdapter.add("No teachers");
            }

        }
        else {
            //создаем лист групп
            if (Groups.groupsLists.size() != 0) {
                //добавляем в массив из класса Groups группы
                idElements = new int[Groups.groupsLists.size()];
                for (int j = 0; j < Groups.groupsLists.size(); ++j) {
                    listAdapter.add(Groups.groupsLists.get(j).GroupName);
                    idElements[j] = Groups.groupsLists.get(j).id;
                    // Если id группы совпадает с id группы пользователя - эта группа и будет показана первой
                    if (user.groupId == idElements[j])
                        selectFirst = j;
                }
            } else {
                //в том случае если групп по курсу нету
                listAdapter.add("No groups");
            }
        }

        //устанавливаем спинер
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_schedule_layout, listAdapter);
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_white);
        spinner.setAdapter(dataAdapter);
        spinner.setSelection(selectFirst);
        spinner.setOnItemSelectedListener(this);
    }

    //если в спинере была выбрана группа
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedItemId = (int)id;
        setSchedule(idElements[selectedItemId]);
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
        setSchedule(idElements[selectedItemId]);
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

    //устанавливаем расписание
    protected void setSchedule(int id) {
        ArrayList<ScheduleList> scheduleList = entity.equals("teachers") ? Teachers.findById(id).scheduleList : Groups.findById(id).scheduleList;
        clearSchedule();

        //получаем неделю в зависимости от выбранной недели
        int numType = type_week.getText().equals(getResources().getString(R.string.denominator)) ? 1 : 0;

        for (int i = 0; i < scheduleList.size(); ++i) {
            ScheduleList list = scheduleList.get(i); // Чтобы не вызывать постоянно метод get (Код будет выглядеть короче)

            if (list.half == numType)
                try {
                    //парсим предмет по установленому языку в приложении
                    JSONObject subjectJSONObject = new JSONObject(list.subject);
                    String subject = subjectJSONObject.getString(Locale.getDefault().getLanguage());

                    JSONObject typeJSONObject = new JSONObject(list.type);
                    String type = typeJSONObject.getString(Locale.getDefault().getLanguage());

                    scheduleTextView[list.day][list.pair].setText(subject + " " + type + " (" + list.room + ")");
                } catch (Exception e) {
                    // Не смогло преобразовать текстовое поле
                    scheduleTextView[list.day][list.pair].setText("JSON parse Error");
                }
        }
    }

    protected void clearSchedule() {
        for (int i = 0; i < MAX_DAYS; ++i)
            for (int j = 0; j < MAX_PAIR; ++j)
                scheduleTextView[i][j].setText("");
    }
}

