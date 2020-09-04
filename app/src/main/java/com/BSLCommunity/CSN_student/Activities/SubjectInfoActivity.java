package com.BSLCommunity.CSN_student.Activities;

import android.animation.Animator;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import androidx.appcompat.app.AppCompatActivity;
import androidx.interpolator.view.animation.FastOutLinearInInterpolator;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;

import com.BSLCommunity.CSN_student.Managers.AnimationManager;
import com.BSLCommunity.CSN_student.Managers.LocaleHelper;
import com.BSLCommunity.CSN_student.Objects.Subjects;
import com.BSLCommunity.CSN_student.Objects.SubjectsInfo;
import com.BSLCommunity.CSN_student.Objects.Teachers;
import com.BSLCommunity.CSN_student.R;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Locale;

public class SubjectInfoActivity extends BaseActivity implements AdapterView.OnItemSelectedListener, SubjectInfoDialogEditText.DialogListener {
    ArrayList<Integer> teacherIds;  //список учителей для установки
    SubjectsInfo.SubjectInfo subjectInfo = null;
    LinearLayout labsLL, ihwLL, otherLL;   // Выпадающие списки работ
    int subjectId; //id предмета. Ставится в классе SubjectList
    LinearLayout rootContainer; //элемент в котором находятся все объекты

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnimationManager.setAnimation(getWindow(), this);
        setContentView(R.layout.activity_subject_info);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //получем необходимые объекты
        subjectId = (getIntent().getIntExtra("button_id", 0));
        subjectInfo = SubjectsInfo.getInstance(this).subjectInfo[subjectId];

        labsLL = findViewById(R.id.activity_subject_info_ll_labs);
        ihwLL = findViewById(R.id.activity_subject_info_ll_ihw);
        otherLL = findViewById(R.id.activity_subject_info_ll_other);

        rootContainer = findViewById(R.id.activity_subject_info_ll_main);

        setSubjectName(); //ставим имя предмета
        createValueSpinner(); //создаем спиннер ценностей предмета
        setTeachers(); // Устанавливаем учителей

        loadData(); // Загружаем данные
    }

    //функция вызывается при закрытие активити, в которой идет сохранение данных
    @Override
    protected void onPause() {
        saveWorkNames();
        SubjectsInfo.getInstance(this).save(this); //сохраняем в JSON
        super.onPause();
    }

    public void saveWorkNames() {
        // Используется для ссылок на родительский элемент для каждой группы
        TableLayout parentTL;

        // Сохранение названий лабораторных работ
        parentTL = findViewById(R.id.activity_subject_info_tb_labs_data);
        for (int i = 0; i < parentTL.getChildCount(); ++i) {
            TableRow row = (TableRow) parentTL.getChildAt(i);
            subjectInfo.labs.names.set(i, ((EditText) row.getChildAt(0)).getText().toString());
            subjectInfo.labs.marks.set(i, Integer.parseInt(((EditText) row.getChildAt(2)).getText().toString()));
        }

        // Сохранение названий ИДЗ
        parentTL = findViewById(R.id.activity_subject_info_tb_ihw_data);
        for (int i = 0; i < parentTL.getChildCount(); ++i) {
            TableRow row = (TableRow) parentTL.getChildAt(i);
            subjectInfo.ihw.names.set(i, ((EditText) row.getChildAt(0)).getText().toString());
            subjectInfo.ihw.marks.set(i, Integer.parseInt(((EditText) row.getChildAt(2)).getText().toString()));
        }

        // Сохранение названий "других" работ
        parentTL = findViewById(R.id.activity_subject_info_tb_other_data);
        for (int i = 0; i < parentTL.getChildCount(); ++i) {
            TableRow row = (TableRow) parentTL.getChildAt(i);
            subjectInfo.others.names.set(i, ((EditText) row.getChildAt(0)).getText().toString());
            subjectInfo.others.marks.set(i, Integer.parseInt(((EditText) row.getChildAt(2)).getText().toString()));
        }
    }

    //устанавлиаем название предмета
    public void setSubjectName() {
        Button subjectNameBtn = (Button) findViewById(R.id.activity_subject_info_bt_subjectName);
        try {
            JSONObject subjectJSONObject = new JSONObject(Subjects.subjectsList[subjectId].NameDiscipline);
            subjectNameBtn.setText(subjectJSONObject.getString(LocaleHelper.getLanguage(this)));
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

        parent.setBackgroundResource(SubjectsInfo.colors[(int)id]); // Устанавка цвета относительно выбранного варианта
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
        Button[] btTeachers = new Button[] {btLector, btPractice, btAssistant};


        // Устаанавлваем имена преподавателей
        int i;
        for (i = 0; i < teacherIds.size(); ++i) {
            try {
                JSONObject teacherJSONObject = new JSONObject(Teachers.findById(teacherIds.get(i)).FIO);
                btTeachers[i].setText(teacherJSONObject.getString(LocaleHelper.getLanguage(this)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Скрываем поля если нету преподавателя
        for (; i < 3; ++i)
            ((View) btTeachers[i].getParent()).setVisibility(View.GONE);
    }

    // Установка прогресса в нижней части экрана
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

    // Загрузка данных
    public void loadData() {

        //добавляем полосы работ (лаб, идз и прочего)
        for (int i = 0; i < subjectInfo.labs.count; ++i)
            drawElementWork((TableLayout) findViewById(R.id.activity_subject_info_tb_labs_data), subjectInfo.labs);
        for (int i = 0; i < subjectInfo.ihw.count; ++i)
            drawElementWork((TableLayout) findViewById(R.id.activity_subject_info_tb_ihw_data), subjectInfo.ihw);
        for (int i = 0; i < subjectInfo.others.count; ++i)
            drawElementWork((TableLayout) findViewById(R.id.activity_subject_info_tb_other_data), subjectInfo.others);

        //ставим прогресс
        setProgress();
    }

    boolean isOpenLab = false, isOpenIHW = false, isOpenOther = false; // Флаги показывающие открыта ли вкладка с работой или нет (лабораторные, ИДЗ, другие)

    // Открываем список какой либо работы
    public void openWork(View view) {
        Button addBt = null;
        TableLayout infoTL = null;

        TransitionManager.beginDelayedTransition(rootContainer);

        switch (view.getId()) {
            case R.id.activity_subject_info_bt_labs:
                addBt = findViewById(R.id.activity_subject_info_bt_add_lab);
                infoTL = findViewById(R.id.activity_subject_info_tb_labs_data);
                isOpenLab = !isOpenLab;
                drawWork(addBt, infoTL, isOpenLab, labsLL);
                break;
            case R.id.activity_subject_info_bt_ihw:
                addBt = findViewById(R.id.activity_subject_info_bt_add_ihw);
                infoTL = findViewById(R.id.activity_subject_info_tb_ihw_data);
                isOpenIHW = !isOpenIHW;
                drawWork(addBt, infoTL, isOpenIHW, ihwLL);
                break;
            case R.id.activity_subject_info_bt_other:
                addBt = findViewById(R.id.activity_subject_info_bt_add_other);
                infoTL = findViewById(R.id.activity_subject_info_tb_other_data);
                isOpenOther = !isOpenOther;
                drawWork(addBt, infoTL, isOpenOther, otherLL);
                break;
        }
    }

    // Открываем вкладку с выбраным типо работы
    private void drawWork(final Button addBt, final TableLayout infoTL, final boolean isOpen, final LinearLayout linearLayout) {
       /* Transition slide = new Slide(Gravity.TOP).addTarget(addBt).addTarget(infoTL).setDuration(1000).setInterpolator(isOpen ?
                new LinearOutSlowInInterpolator() :
                new FastOutLinearInInterpolator());*/

        Animator.AnimatorListener listener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                TransitionManager.beginDelayedTransition(rootContainer);
                if(!isOpen){
                    addBt.setVisibility(View.GONE);
                    infoTL.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        };

        addBt.animate().alpha(isOpen ? 1.0f : 0.0f).setDuration(getResources().getInteger(R.integer.subject_info_animation_duration)).setInterpolator(isOpen ?
                new LinearOutSlowInInterpolator() :
                new FastOutLinearInInterpolator()).setListener(listener);

        infoTL.animate().alpha(isOpen ? 1.0f : 0.0f).setDuration(getResources().getInteger(R.integer.subject_info_animation_duration)).setInterpolator(isOpen ?
                new LinearOutSlowInInterpolator() :
                new FastOutLinearInInterpolator()).setListener(listener);

        if(isOpen){
            addBt.setVisibility(View.VISIBLE);
            infoTL.setVisibility(View.VISIBLE);
        }
    }

    // Добавляет строчку с работой (Функция пренадлежит кнопке "+")
    public void addElementWork(View view) {
        TableLayout infoTL = null; // Группа элементов
        SubjectsInfo.SubjectInfo.Work work = null; // Ссылка на необходимую группу работ

        TransitionManager.beginDelayedTransition(rootContainer);

        // Определяем тип предмета
        switch (view.getId()) {
            case R.id.activity_subject_info_bt_add_lab:
                infoTL = findViewById(R.id.activity_subject_info_tb_labs_data);
                work = subjectInfo.labs;
                break;
            case R.id.activity_subject_info_bt_add_ihw:
                infoTL = findViewById(R.id.activity_subject_info_tb_ihw_data);
                work = subjectInfo.ihw;
                break;
            case R.id.activity_subject_info_bt_add_other:
                infoTL = findViewById(R.id.activity_subject_info_tb_other_data);
                work = subjectInfo.others;
                break;
        }

        work.addWork();// Добавление пустого элемента в группу выбранной работы
        drawElementWork(infoTL, work);// Прорисовка элемента
        setProgress();// Обновление прогресса
    }

    /* Отрисовка элемента
    * Параметры:
    * infoTL - группа элементов в которой необходимо отрисовать элемент
    * work - группа работ (лабораторные, ИДЗ, другие)
    * */
    private void drawElementWork(TableLayout infoTL, SubjectsInfo.SubjectInfo.Work work) {
        int num = infoTL.getChildCount(); // Номер работы
        TableRow elementWork = (TableRow) LayoutInflater.from(this).inflate(R.layout.inflate_work_element, null); // Строка работы

        // Устанавливаем текст работы
        EditText textName = (EditText) elementWork.getChildAt(0);
        textName.setText(work.names.get(num));

        // Устанавливаем статус работы в спиннере
        Spinner spinner = (Spinner) elementWork.getChildAt(1);
        spinner.setSelection(work.values.get(num));
        spinner.setOnItemSelectedListener(this);

        // Установка количества баллов за работу
        EditText textMark = (EditText) elementWork.getChildAt(2);
        textMark.setText(work.marks.get(num).toString());

        infoTL.addView(elementWork);
    }

    // Удаление одной работы
    public void deleteElementWork(View view) {
        final TableRow workRow = (TableRow)(view.getParent()); // Строчка работы в списке
        final TableLayout infoTL = ((TableLayout)(workRow.getParent())); // Группа
        int num = infoTL.indexOfChild(workRow); // Номер работы

        TransitionManager.beginDelayedTransition(rootContainer);

        // Удваление работы в зависимости от выбранной группы
        switch (infoTL.getId()) {
            case R.id.activity_subject_info_tb_labs_data:
                subjectInfo.labs.deleteWork(num);
                break;
            case R.id.activity_subject_info_tb_ihw_data:
                break;
            case R.id.activity_subject_info_tb_other_data:
                subjectInfo.others.deleteWork(num);
                break;
        }

        infoTL.removeView(workRow);
    }

    @Override
    public void applyText(String text, int number, Button name) {
        name.setText(text);
    }

    private void mGetTeacherId(int id){
        if(id!=0) teacherIds.add(id);
    }
}
