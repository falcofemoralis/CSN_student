package com.BSLCommunity.CSN_student.Activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;

import androidx.appcompat.app.AppCompatActivity;

import com.BSLCommunity.CSN_student.Objects.Subjects;
import com.BSLCommunity.CSN_student.Objects.SubjectsInfo;
import com.BSLCommunity.CSN_student.Objects.Teachers;
import com.BSLCommunity.CSN_student.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class SubjectInfo extends AppCompatActivity implements AdapterView.OnItemSelectedListener, SubjectInfoDialogEditText.DialogListener {
    //тип работы
    public enum Types {
        lab,
        ihw,
        other

    }
    public final  int TYPES_COUNT = 3;

    int[] colors = { //цвета кнопок выбора
            R.color.not_passed,
            R.color.in_process,
            R.color.done_without_report,
            R.color.done_with_report,
            R.color.waiting_acceptation,
            R.color.passed_without_report,
            R.color.passed_with_report};

    ArrayList<Integer> teacherIds;  //список учителей для установки

    SubjectsInfo.SubjectInfo subjectInfo = null;

    // Выпадающие списки работ
    LinearLayout labsLL, ihwLL, otherLL;

    int subjectId; //id предмета. Ставится в классе SubjectList


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_info);

       /* headTexts[0] = (TextView) findViewById(R.id.activity_subject_info_tv_labs_headText);
        headTexts[1] = (TextView) findViewById(R.id.activity_subject_info_tv_ihw_headText);
        headTexts[2] = (TextView) findViewById(R.id.activity_subject_info_tv_other_headText);

        workNames[0] = getResources().getString(R.string.Lab);
        workNames[1] = getResources().getString(R.string.IHW);
        workNames[2] = getResources().getString(R.string.other); */

        //получем необходимые объекты
        subjectId = (getIntent().getIntExtra("button_id", 0));
        subjectInfo = SubjectsInfo.getInstance(this).subjectInfo[subjectId];

        labsLL = findViewById(R.id.activity_subject_info_ll_labs);
        ihwLL = findViewById(R.id.activity_subject_info_ll_ihw);
        otherLL = findViewById(R.id.activity_subject_info_ll_other);

        setSubjectName(); //ставим имя предмета
        createValueSpinner(); //создаем спиннер ценностей предмета
        setTeachers(); // Устанавливаем учителей

        loadData(); // Загружаем данные
    }

    //функция вызывается при закрытие активити, в которой идет сохранение данных
    @Override
    protected void onPause() {
        SubjectsInfo.getInstance(this).save(this); //сохраняем в JSON
        super.onPause();
    }

    //устанавлиаем название предмета
    public void setSubjectName() {
        Button subjectNameBtn = (Button) findViewById(R.id.activity_subject_info_bt_subjectName);
        try {
            JSONObject subjectJSONObject = new JSONObject(Subjects.subjectsList[subjectId].NameDiscipline);
            subjectNameBtn.setText(subjectJSONObject.getString(Locale.getDefault().getLanguage()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //спине выбора ценности предмета
    protected void createValueSpinner() {
        Spinner valueSpinner = findViewById(R.id.activity_subject_info_sp_values);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(
                this,
                R.array.values,
                R.layout.spinner_subjectinfo_layout
        );
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_white);
        valueSpinner.setAdapter(adapter);

        try {
            valueSpinner.setSelection(subjectInfo.subjectValue);
        } catch (Exception e) {
            valueSpinner.setSelection(0);
        }

        valueSpinner.setOnItemSelectedListener(this);
    }

    //выбор элемента на спинерах
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        // Вызов был инициирован спиннером выбора статуса предмета - устаналиваем статус (экз, зачет, диф зачет)
        if (parent.getId() == R.id.activity_subject_info_sp_values) {
            subjectInfo.subjectValue = (int) id; //получаем id ценности работы 0...6
            return;
        }

        // Вызов был инициирован спиннером выбора статуса какой либо из работы
        TableRow elementWork = (TableRow) parent.getParent(); // Получаем сам элемент
        TableLayout parentTL = (TableLayout) parent.getParent().getParent(); // Получаем группу в которой находится элемент

        // Определяем вид работы и устанавливаем её статус
        switch (parentTL.getId()) {
            case R.id.activity_subject_info_tb_labs_data:
                subjectInfo.labs.values.set(parentTL.indexOfChild(elementWork), (int)id);
                break;
            case R.id.activity_subject_info_tb_ihw_data:
                subjectInfo.ihw.values.set(parentTL.indexOfChild(elementWork), (int)id);
                break;
            case R.id.activity_subject_info_tb_other_data:
                subjectInfo.others.values.set(parentTL.indexOfChild(elementWork), (int)id);
                break;
        }

        parent.setBackgroundResource(colors[(int)id]); // Устанавка цвета относительно выбранного варианта
        setProgress(); // Обновление прогресса
    }

    //нужен для реализации интерфейса AdapterView.OnItemSelectedListener
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    //устанавливаем преподователей
    public void setTeachers() {
        //получаем id преподов
        teacherIds = new ArrayList<>(); //список преподов для установки
        mGetTeacherId(Subjects.subjectsList[subjectId].Code_Lector);
        mGetTeacherId(Subjects.subjectsList[subjectId].Code_Practice);
        mGetTeacherId(Subjects.subjectsList[subjectId].Code_Assistant);


        // Поля где необходимо установить имена преподавателей
        Button btLector = findViewById(R.id.activity_subject_info_bt_lector);
        Button btPractice = findViewById(R.id.activity_subject_info_bt_practice);
        Button btAssistant = findViewById(R.id.activity_subject_info_bt_assistant);
        Button[] btTeachers = new Button[]{btLector, btPractice, btAssistant};


        // Устаанавлваем имена преподавателей
        for (int i = 0; i < teacherIds.size(); ++i) {
            try {
                JSONObject teacherJSONObject = new JSONObject(Teachers.findById(teacherIds.get(i)).FIO);
                btTeachers[i].setText(teacherJSONObject.getString(Locale.getDefault().getLanguage()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //устанавливаем прогресс в нижней части экрана
    private void setProgress() {
        Button progress = (Button) findViewById(R.id.activity_subject_info_bt_progress);

        int completed = 0;
        // Считаем количество завершенных лаб (статус которы "Сдано с отчетом")
        for (int i = 0; i < subjectInfo.labs.values.size(); ++i)
            if (subjectInfo.labs.values.get(i) == 6)
                completed++;

        // Считаем количество завершенных ИДЗ (статус которы "Сдано с отчетом")
        for (int i = 0; i < subjectInfo.ihw.values.size(); ++i)
            if (subjectInfo.ihw.values.get(i) == 6)
                completed++;

        // Считаем количество завершенных других работ (статус которы "Сдано с отчетом")
        for (int i = 0; i <  subjectInfo.others.values.size(); ++i)
            if (subjectInfo.others.values.get(i) == 6)
                completed++;

        // Устанравливаем прогресс предмета
        try {
            progress.setText(Integer.toString(completed * 100 / (subjectInfo.labs.count +  subjectInfo.ihw.count +  subjectInfo.others.count)) + "%");
        } catch (Exception e) {
            System.out.println(e);
            progress.setText("0%");
        }
    }

    //загружаем работы
    public void loadData() {

        //добавляем полосы работ (лаб, идз и прочего)
        for (int i = 0; i < subjectInfo.labs.count; ++i)
            drawElementWork((TableLayout) findViewById(R.id.activity_subject_info_tb_labs_data), getString(R.string.lab), subjectInfo.labs.values.get(i));
        for (int i = 0; i < subjectInfo.ihw.count; ++i)
            drawElementWork((TableLayout) findViewById(R.id.activity_subject_info_tb_ihw_data), getString(R.string.ihw), subjectInfo.ihw.values.get(i));
        for (int i = 0; i < subjectInfo.others.count; ++i)
            drawElementWork((TableLayout) findViewById(R.id.activity_subject_info_tb_other_data), getString(R.string.other), subjectInfo.others.values.get(i));

        //ставим прогресс
        setProgress();
    }

    boolean isOpenLab = false, isOpenIHW = false, isOpenOther = false; // Флаги показывающие открыта ли вкладка с работой или нет (лабораторные, ИДЗ, другие)
    // Открываем список какой либо работы
    public void openWork(View view) {

        Button addBt = null;
        TableLayout infoTL = null;

        switch (view.getId()) {
            case R.id.activity_subject_info_bt_labs:
                addBt = findViewById(R.id.activity_subject_info_bt_add_lab);
                infoTL = findViewById(R.id.activity_subject_info_tb_labs_data);
                isOpenLab = !isOpenLab;
                drawWork(addBt, infoTL, isOpenLab);
                break;
            case R.id.activity_subject_info_bt_ihw:
                addBt = findViewById(R.id.activity_subject_info_bt_add_ihw);
                infoTL = findViewById(R.id.activity_subject_info_tb_ihw_data);
                isOpenIHW = !isOpenIHW;
                drawWork(addBt, infoTL, isOpenIHW);
                break;
            case R.id.activity_subject_info_bt_other:
                addBt = findViewById(R.id.activity_subject_info_bt_add_other);
                infoTL = findViewById(R.id.activity_subject_info_tb_other_data);
                isOpenOther = !isOpenOther;
                drawWork(addBt, infoTL, isOpenOther);
                break;
        }
    }

    // Открываем вкладку с выбраным типо работы
    private void drawWork(Button addBt, TableLayout infoTL, boolean isOpen) {
        if (isOpen) {
            addBt.setVisibility(View.VISIBLE);
            infoTL.setVisibility(View.VISIBLE);
        }
        else {
            addBt.setVisibility(View.GONE);
            infoTL.setVisibility(View.GONE);
        }
    }

    // Добавляет строчку с работой (Функция пренадлежит кнопке "+")
    public void addElementWork(View view) {
        TableLayout infoTL = null; // Группа элементов
        String textElement = null; // Текст элемента (Пока что устанавливается так)

        // Определяем тип предмета
        switch (view.getId()) {
            case R.id.activity_subject_info_bt_add_lab:
                infoTL = findViewById(R.id.activity_subject_info_tb_labs_data);
                textElement = getString(R.string.lab);
                subjectInfo.labs.addWork();;
                break;
            case R.id.activity_subject_info_bt_add_ihw:
                infoTL = findViewById(R.id.activity_subject_info_tb_ihw_data);
                textElement = getString(R.string.ihw);
                subjectInfo.ihw.addWork();;
                break;
            case R.id.activity_subject_info_bt_add_other:
                infoTL = findViewById(R.id.activity_subject_info_tb_other_data);
                textElement = getString(R.string.other);
                subjectInfo.others.addWork();;
                break;
        }

        // Рисуем элемент
        drawElementWork(infoTL, textElement, 0);

        // Обновляем прогресс
        setProgress();
    }

    /* Отрисовка элемента
    * Параметры:
    * infoTL - группа элементов в которой необходимо отрисовать элемент
    * textElement - текст для элемента работы (к примеру "Лабораторная")
    * spinnerValue - если отрисовка использует сохраненные данные, то необходимо восстановить сохраненный статус работы
    * */
    private void drawElementWork(TableLayout infoTL, String textElement, int spinnerValue) {
        TableRow elementWork = (TableRow) LayoutInflater.from(this).inflate(R.layout.inflate_work_element, null);

        // Устанавливаем текст работы
        Button textViewElement = (Button) elementWork.getChildAt(0);
        textViewElement.setText(textElement + ' ' + (infoTL.getChildCount() + 1));
        infoTL.addView(elementWork);

        // Устанавливаем статус работы в спиннере
        Spinner spinner = (Spinner) elementWork.getChildAt(1);
        spinner.setSelection(spinnerValue);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void applyText(String text, Types type, int number, Button name) {
        name.setText(text);
    }

    /*
    //удаляем работу
    private void removeWorkRow(int id, int count, ArrayList<Integer> countValues) {
        LinearLayout linearLayout = (LinearLayout) findViewById(id);
        linearLayout.removeViewAt((count * 2) - 1); //удаляем пробел (последняя позиция)
        linearLayout.removeViewAt((count * 2) - 2); //удаляем TableRow с кнопками лабы (предпоследняя позиция)
        countValues.remove(count - 1);
    }

    //режим добавление\удаления работ
    public void refactorOnClick(View view) {
        for (int i = 0; i < tableRows.length; ++i)
            changeState(mGetTableRowId(tableRows[i]), isClicked);
        isClicked = !isClicked;
    }
    */

    //меняем режим  добавление\удаления работ
    private void changeState(TableRow tableRow, boolean state) {
        if (state) tableRow.setVisibility(View.GONE);
        else tableRow.setVisibility(View.VISIBLE);
    }

    //получение view TableRow по id
    private TableRow mGetTableRowId(int id) {
        return findViewById(id);
    }

    private void mGetTeacherId(int id){
        if(id!=0) teacherIds.add(id);
    }
}
