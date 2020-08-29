package com.BSLCommunity.CSN_student.Activities;

import android.os.Bundle;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

public class SubjectInfoActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, SubjectInfoDialogEditText.DialogListener {
    //тип работы
    public enum Types {
        lab,
        ihw,
        other

    }

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
    LinearLayout labsLL, ihwLL, otherLL;   // Выпадающие списки работ
    int subjectId; //id предмета. Ставится в классе SubjectList

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAnimation();
        setContentView(R.layout.activity_subject_info);

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
        Button[] btTeachers = new Button[] {btLector, btPractice, btAssistant};


        // Устаанавлваем имена преподавателей
        int i;
        for (i = 0; i < teacherIds.size(); ++i) {
            try {
                JSONObject teacherJSONObject = new JSONObject(Teachers.findById(teacherIds.get(i)).FIO);
                btTeachers[i].setText(teacherJSONObject.getString(Locale.getDefault().getLanguage()));
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
        Animation falling_down = AnimationUtils.loadAnimation(this, R.anim.falling_down); //анимация выпадания
        Animation falling_up = AnimationUtils.loadAnimation(this, R.anim.falling_up); //анимация западания

        Button addBt = null;
        TableLayout infoTL = null;

        switch (view.getId()) {
            case R.id.activity_subject_info_bt_labs:
                addBt = findViewById(R.id.activity_subject_info_bt_add_lab);
                infoTL = findViewById(R.id.activity_subject_info_tb_labs_data);
                isOpenLab = !isOpenLab;
                drawWork(addBt, infoTL, isOpenLab);
                if(isOpenLab) {
                    ihwLL.setAnimation(falling_down);
                    otherLL.setAnimation(falling_down);
                }
                else {
                    ihwLL.setAnimation(falling_up);
                    otherLL.setAnimation(falling_up);
                }
                break;
            case R.id.activity_subject_info_bt_ihw:
                addBt = findViewById(R.id.activity_subject_info_bt_add_ihw);
                infoTL = findViewById(R.id.activity_subject_info_tb_ihw_data);
                isOpenIHW = !isOpenIHW;
                drawWork(addBt, infoTL, isOpenIHW);
                if(isOpenIHW) otherLL.setAnimation(falling_down);
                else otherLL.setAnimation(falling_up);
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
    private void drawWork(final Button addBt, final TableLayout infoTL, boolean isOpen) {
        Animation alpha_falling_down = AnimationUtils.loadAnimation(this, R.anim.alpha_falling_down); //анимация выпадания
        Animation alpha_falling_up = AnimationUtils.loadAnimation(this, R.anim.alpha_falling_up); //анимация западания

        if (isOpen) {
            infoTL.setVisibility(View.VISIBLE);
            infoTL.startAnimation(alpha_falling_down);
            addBt.setVisibility(View.VISIBLE);
            addBt.startAnimation(alpha_falling_down);

        }
        else {
            addBt.startAnimation(alpha_falling_up);
            infoTL.startAnimation(alpha_falling_up);

            alpha_falling_up.setAnimationListener(new Animation.AnimationListener(){
                @Override
                public void onAnimationStart(Animation arg0) { }
                @Override
                public void onAnimationRepeat(Animation arg0) { }
                @Override
                public void onAnimationEnd(Animation arg0) {
                    addBt.setVisibility(View.GONE);
                    infoTL.setVisibility(View.GONE);
                }
            });

        }
    }

    // Добавляет строчку с работой (Функция пренадлежит кнопке "+")
    public void addElementWork(View view) {
        TableLayout infoTL = null; // Группа элементов
        SubjectsInfo.SubjectInfo.Work work = null; // Ссылка на необходимую группу работ
        Animation falling_down = AnimationUtils.loadAnimation(this, R.anim.falling_down); //анимация выпадания

        // Определяем тип предмета
        switch (view.getId()) {
            case R.id.activity_subject_info_bt_add_lab:
                infoTL = findViewById(R.id.activity_subject_info_tb_labs_data);
                work = subjectInfo.labs;
                ihwLL.startAnimation(falling_down);
                otherLL.startAnimation(falling_down);
                break;
            case R.id.activity_subject_info_bt_add_ihw:
                infoTL = findViewById(R.id.activity_subject_info_tb_ihw_data);
                work = subjectInfo.ihw;
                otherLL.startAnimation(falling_down);
                break;
            case R.id.activity_subject_info_bt_add_other:
                infoTL = findViewById(R.id.activity_subject_info_tb_other_data);
                work = subjectInfo.others;
                break;
        }

        view.startAnimation(falling_down);
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
        elementWork.startAnimation(AnimationUtils.loadAnimation(this, R.anim.alpha_falling_down));
    }

    // Удаление одной работы
    public void deleteElementWork(View view) {
        final TableRow workRow = (TableRow)(view.getParent()); // Строчка работы в списке
        final TableLayout infoTL = ((TableLayout)(workRow.getParent())); // Группа
        int num = infoTL.indexOfChild(workRow); // Номер работы
        Animation falling_up = AnimationUtils.loadAnimation(this, R.anim.falling_up); //анимация захождения

        // Удваление работы в зависимости от выбранной группы
        switch (infoTL.getId()) {
            case R.id.activity_subject_info_tb_labs_data:
                subjectInfo.labs.deleteWork(num);
                ihwLL.startAnimation(falling_up);
                otherLL.startAnimation(falling_up);
                break;
            case R.id.activity_subject_info_tb_ihw_data:
                subjectInfo.ihw.deleteWork(num);
                otherLL.startAnimation(falling_up);
                break;
            case R.id.activity_subject_info_tb_other_data:
                subjectInfo.others.deleteWork(num);

                break;
        }
        view.startAnimation(falling_up);

        infoTL.removeView(workRow);
    }

    @Override
    public void applyText(String text, Types type, int number, Button name) {
        name.setText(text);
    }

    private void mGetTeacherId(int id){
        if(id!=0) teacherIds.add(id);
    }

    public void setAnimation() {
        Slide slide = new Slide();
        slide.setSlideEdge(Gravity.RIGHT);
        slide.setDuration(400);
        slide.setInterpolator(new AccelerateDecelerateInterpolator());
        getWindow().setExitTransition(slide);
        getWindow().setEnterTransition(slide);
    }
}
