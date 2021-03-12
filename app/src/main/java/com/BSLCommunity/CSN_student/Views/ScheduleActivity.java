package com.BSLCommunity.CSN_student.Views;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.BSLCommunity.CSN_student.Managers.AnimationManager;
import com.BSLCommunity.CSN_student.Managers.LocaleHelper;
import com.BSLCommunity.CSN_student.Models.ScheduleList;
import com.BSLCommunity.CSN_student.Presenters.SchedulePresenter;
import com.BSLCommunity.CSN_student.R;
import com.BSLCommunity.CSN_student.ViewInterfaces.ScheduleView;

import org.json.JSONObject;

import java.util.ArrayList;


//форма расписание предметов группы
public class ScheduleActivity extends BaseActivity implements ScheduleView, AdapterView.OnItemSelectedListener {
    private final int MAX_PAIR = 5; //кол-во пар в активити
    private final int MAX_DAYS = 5; //кол-во дней в активити
    private final int DEFAULT_HALF = 0; // Числитель

    /* //массив из элементов TextView в активити
    ScheduleList[][][] scheduleList;  //сохраненое расписание

    int[] idElements; // id сущностей в спиннере (в порядке расположения их в спиннере)
    int selectedItemId; // ID выбранного элемента в спиннере

    ProgressBar progressBar; //анимация загрузки в спиннере*/


    private SchedulePresenter schedulePresenter;
    private Spinner spinner;
    private TextView weekTypeView; //тип недели
    private TextView[][] scheduleTextView = new TextView[MAX_DAYS][MAX_PAIR];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lessons_schedule);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        AnimationManager.setAnimation(getWindow(), this);

        weekTypeView = findViewById(R.id.activity_lessons_schedule_bt_weekType);

        //получеам id текстовых полей с activity_lessons_schedule и сохраняем их в массиве schedule[][]
        for (int i = 0; i < MAX_DAYS; ++i)
            for (int j = 0; j < MAX_PAIR; ++j)
                scheduleTextView[i][j] = findViewById(getResources().getIdentifier("text_" + i + "_" + j, "id", getApplicationContext().getPackageName()));

        schedulePresenter = new SchedulePresenter(this, (SchedulePresenter.EntityTypes) getIntent().getSerializableExtra("EntityTypes"));
        schedulePresenter.initSpinnerData();
        schedulePresenter.initSchedule(DEFAULT_HALF);

        //TODO сделать определение чистил или знам



 /*       progressBar = (ProgressBar) findViewById(R.id.activity_lessons_schedule_pb_main);
        Sprite iIndeterminateDrawable = new ThreeBounce();
        iIndeterminateDrawable.setColor(getColor(R.color.white));
        progressBar.setIndeterminateDrawable(iIndeterminateDrawable);
 */


        //createSpinner();
        // getScheduleElements();

    }


    //если в спинере была выбрана группа
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        schedulePresenter.changeGroup((String) spinner.getItemAtPosition(position));
   /*     selectedItemId = (int)id;
        try {
            setSchedule(idElements[selectedItemId]);
        }catch (Exception e){
            e.getStackTrace();
        }*/
    }

    public void changeWeekType(View v) {
        TransitionManager.beginDelayedTransition((LinearLayout) findViewById(R.id.activity_lessons_schedule_ll_main));

        if (weekTypeView.getText().equals(getResources().getString(R.string.denominator))){
            weekTypeView.setText(getResources().getString(R.string.numerator));
            schedulePresenter.changeHalf(0);
        }
        else{
            weekTypeView.setText(getResources().getString(R.string.denominator));
            schedulePresenter.changeHalf(1);
        }
    }

    //нужен для реализации интерфейса AdapterView.OnItemSelectedListener
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void setSpinnerData(ArrayList<String> entities, int defaultGroup) {
        spinner = findViewById(R.id.activity_lessons_schedule_sp_main);
        spinner.setPrompt(getString(R.string.group_prompt));
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_schedule_groups_layout, entities);
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_white);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(defaultGroup);
    }

    @Override
    public void setSchedule(ArrayList<ScheduleList> scheduleList, int half) {
        clearSchedule();

        for (int i = 0; i < scheduleList.size(); ++i) {
            ScheduleList list = scheduleList.get(i);

            if(list.half == 2 || list.half == half){
                try {
                    String subject = (new JSONObject(list.subject)).getString(LocaleHelper.getLanguage(getApplicationContext()));
                    String type = (new JSONObject(list.type)).getString(LocaleHelper.getLanguage(getApplicationContext()));
                    scheduleTextView[list.day-1][list.pair-1].setText(subject + " " + type + " (" + list.room + ")");
                } catch (Exception e){
                    Log.d("PARSE_ERROR", "No translation for " + list.subject);
                }
            }
        }
    }

    @Override
    public void clearSchedule() {
        for (int i = 0; i < MAX_DAYS; ++i)
            for (int j = 0; j < MAX_PAIR; ++j)
                scheduleTextView[i][j].setText("");
    }


/*    //меняем тип недели
    public void changeTypeWeek(View v) {
        TransitionManager.beginDelayedTransition((LinearLayout) findViewById(R.id.activity_lessons_schedule_ll_main));

        if (type_week.getText().equals(getResources().getString(R.string.denominator)))
            type_week.setText(getResources().getString(R.string.numerator));
        else
            type_week.setText(getResources().getString(R.string.denominator));

        try {
            setSchedule(idElements[selectedItemId]);
        }catch (Exception e){
            e.getStackTrace();
        }
    }*/


/*    //создание спиннера групп
    protected void createSpinner() {
        UserModel user = UserModel.getUserModel(); // Данные пользователя
        int selectFirst = 0; // Для выбора расписания которое будет показано при загрузке самого окна
        List<String> listAdapter = new ArrayList<>(); // Список строк в спиннере
        ArrayAdapter<String> dataAdapter = null;

        if (entity.equals("teachers")) {

            //создаем лист преподов
            if (TeachersModel.teacherLists.size() != 0) {
                progressBar.setVisibility(View.GONE);

                // int listSize = Teachers.teacherLists.size();
                int listSize = 28; // кол-во преподов на кафедре

                //добавляем в массив из класса Teachers преподы
                idElements = new int[listSize];
                for (int j = 0; j < listSize; ++j) {
                    try {
                        JSONObject FIOJson = new JSONObject(TeachersModel.teacherLists.get(j).FIO);

                        // Разбиение ФИО на составные и установка с инициалами
                        String[] fioStrs = FIOJson.getString(LocaleHelper.getLanguage(this)).split(" ");
                        listAdapter.add(fioStrs[2] + " " + fioStrs[0].charAt(0) + ". " + fioStrs[1].charAt(0));
                    } catch (Exception e) {
                    }
                    idElements[j] = TeachersModel.teacherLists.get(j).id;
                }
                spinner.setPrompt(getString(R.string.teachers_prompt));
                spinner.setEnabled(true);
                dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_schedule_layout, listAdapter);
                dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_white);
                spinner.setAdapter(dataAdapter);
                // Выбор первого учителя не имеет смысла
                selectFirst = 0;
            } else {
                //в том случае если групп по курсу нету
                //  listAdapter.add("No teachers");
            }

        } else {
            //создаем лист групп
            if (GroupModel.groups.size() != 0) {
                progressBar.setVisibility(View.GONE);

                //добавляем в массив из класса Groups группы
                idElements = new int[GroupModel.groups.size()];
                for (int j = 0; j < GroupModel.groups.size(); ++j) {
                    listAdapter.add(GroupModel.groups.get(j).groupName);
                    idElements[j] = GroupModel.groups.get(j).id;
                    // Если id группы совпадает с id группы пользователя - эта группа и будет показана первой
                    if (0 == idElements[j])
                        selectFirst = j;
                }
                spinner.setPrompt(getString(R.string.group_prompt));
                spinner.setEnabled(true);
                dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_schedule_groups_layout, listAdapter);
                dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_white);
                spinner.setAdapter(dataAdapter);
            } else {
                //в том случае если групп по курсу нету
                // listAdapter.add("No groups");
            }
        }

        //устанавливаем спинер
        spinner.setSelection(selectFirst);
        spinner.setOnItemSelectedListener(this);
    }


    //получение необходимых полей с активити расписание
    protected void getScheduleElements() {
        type_week = findViewById(R.id.activity_lessons_schedule_bt_weekType);

        //получеам id текстовых полей с activity_lessons_schedule и сохраняем их в массиве schedule[][]
        //i - пары, j - дни
        for (int i = 0; i < MAX_DAYS; ++i) {
            for (int j = 0; j < MAX_PAIR; ++j) {
                scheduleTextView[i][j] = findViewById(getResources().getIdentifier("text_" + i + "_" + j, "id", getApplicationContext().getPackageName()));
            }
        }
    }

    //устанавливаем расписание
    protected void setSchedule(int id) {
        ArrayList<ScheduleList> scheduleList = entity.equals("teachers") ? TeachersModel.findById(id).scheduleList : GroupModel.getGroupModel().findById(id).scheduleList;
        clearSchedule();

        //получаем неделю в зависимости от выбранной недели
        int numType = type_week.getText().equals(getResources().getString(R.string.denominator)) ? 1 : 0;
        final int BOTH_HALF = 2;

        for (int i = 0; i < scheduleList.size(); ++i) {
            ScheduleList list = scheduleList.get(i); // Чтобы не вызывать постоянно метод get (Код будет выглядеть короче)

            if (list.half == numType || list.half == BOTH_HALF)
                try {
                    //парсим предмет по установленому языку в приложении
                    JSONObject subjectJSONObject = new JSONObject(list.subject);
                    String subject = subjectJSONObject.getString(LocaleHelper.getLanguage(this));

                    JSONObject typeJSONObject = new JSONObject(list.type);
                    String type = typeJSONObject.getString(LocaleHelper.getLanguage(this));

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
    }*/
}

