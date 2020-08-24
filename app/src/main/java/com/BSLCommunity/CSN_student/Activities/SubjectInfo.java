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
    int[] colors = { //цвета кнопок выбора
            R.color.not_passed,
            R.color.in_process,
            R.color.done_without_report,
            R.color.done_with_report,
            R.color.waiting_acceptation,
            R.color.passed_without_report,
            R.color.passed_with_report};
    ArrayList<Integer> teacherIds;  //список учителей для установки
    int labsCount = 0, subjectValue = 0, ihwCount = 0, otherCount = 0; //labsCount - кол-во лаб у предмета, subjectValue - ценность предмета, ihwCount - кол-во ИДЗ

    public ArrayList<Integer> labValues = new ArrayList<>(); //значения лаб
    public ArrayList<Integer> ihwValues = new ArrayList<>(); //значения ИДЗ
    public ArrayList<Integer> otherValues = new ArrayList<>(); //значения заметок

    public ArrayList<Button> labNames = new ArrayList<>(); //названия лаб
    public ArrayList<Button> ihwNames = new ArrayList<>(); //названия ИДЗ
    public ArrayList<Button> otherNames = new ArrayList<>(); //названия заметок

    LinearLayout labsButton, ihwButton, otherButton;

    //тип работы
    public enum Types {
        lab,
        ihw,
        other
    }

    SubjectsInfo subjectsInfo; //данные
    int subjectId; //id предмета. Ставится в классе SubjectList

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_info);

        //получем необходимые объекты
        subjectsInfo = SubjectsInfo.getInstance(this);
        subjectId = (getIntent().getIntExtra("button_id", 0));

        labsButton = findViewById(R.id.activity_subject_info_ll_labs);
        ihwButton = findViewById(R.id.activity_subject_info_ll_ihw);
        otherButton = findViewById(R.id.activity_subject_info_ll_other);

        setSubjectName(); //ставим имя предмета
        createValueSpinner(); //создаем спиннер ценностей предмета
        setTeachers();
        loadData();

        //загружаем информацию про кол-во работ
        labsCount = subjectsInfo.subjectInfo[subjectId].labsCount;
        ihwCount = subjectsInfo.subjectInfo[subjectId].ihwCount;
        otherCount = subjectsInfo.subjectInfo[subjectId].otherCount;
    }

    //функция вызывается при закрытие активити, в которой идет сохранение данных
    @Override
    protected void onPause() {
        subjectsInfo.saveSubjectValue(subjectId, subjectValue); //сохраням ценность предмета
        subjectsInfo.saveCount(subjectId, labsCount, ihwCount, otherCount);  //сохраням кол-во
        subjectsInfo.saveData(subjectId, labValues, labsCount, ihwValues, ihwCount, otherValues, otherCount, labNames, ihwNames, otherNames); //сохраням данные
        subjectsInfo.saveSubject(this); //сохраняем в JSON
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
            valueSpinner.setSelection(subjectsInfo.subjectInfo[subjectId].subjectValue);
        } catch (Exception e) {
            valueSpinner.setSelection(subjectValue);
        }

        valueSpinner.setOnItemSelectedListener(this);
    }

    //выбор элемента на спинере
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.activity_subject_info_sp_values)
            subjectValue = (int) id; //получаем id ценности работы 0...6
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
        for (int i = 0; i < labValues.size(); ++i)
            if (labValues.get(i) == 6)
                completed++;

        for (int i = 0; i < ihwValues.size(); ++i)
            if (ihwValues.get(i) == 6)
                completed++;

        for (int i = 0; i < otherValues.size(); ++i)
            if (otherValues.get(i) == 6)
                completed++;

        try {
            progress.setText(Integer.toString(completed * 100 / (labsCount + ihwCount + otherCount)) + "%");
        } catch (Exception e) {
            System.out.println(e);
            progress.setText("0%");
        }
    }

    //загружаем работы
    public void loadData() {

        //добавляем полосы работ (лаб, идз и прочего)
        for (int i = 0; i < labsCount; ++i)
            drawElementWork((TableLayout) findViewById(R.id.activity_subject_info_tb_labs_data), getString(R.string.lab));
        for (int i = 0; i < ihwCount; ++i)
            drawElementWork((TableLayout) findViewById(R.id.activity_subject_info_tb_ihw_data), getString(R.string.ihw));
        for (int i = 0; i < otherCount; ++i)
            drawElementWork((TableLayout) findViewById(R.id.activity_subject_info_tb_other_data), getString(R.string.other));

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

    public void addElementWork(View view) {
        TableLayout infoTL = null;
        String textElement = null;

        switch (view.getId()) {
            case R.id.activity_subject_info_bt_add_lab:
                infoTL = findViewById(R.id.activity_subject_info_tb_labs_data);
                textElement = getString(R.string.lab);
                break;
            case R.id.activity_subject_info_bt_add_ihw:
                infoTL = findViewById(R.id.activity_subject_info_tb_ihw_data);
                textElement = getString(R.string.ihw);
                break;
            case R.id.activity_subject_info_bt_add_other:
                infoTL = findViewById(R.id.activity_subject_info_tb_other_data);
                textElement = getString(R.string.other);
                break;
        }

       drawElementWork(infoTL, textElement);
    }

    private void drawElementWork(TableLayout infoTL, String textElement) {
        TableRow elementWork = (TableRow) LayoutInflater.from(this).inflate(R.layout.inflate_work_element, null);
        Button textViewElement = (Button) elementWork.getChildAt(0);
        textViewElement.setText(textElement + ' ' + (infoTL.getChildCount() + 1));
        infoTL.addView(elementWork);
    }

    /*
    //обработчик добавления новой работы
    public void addOnClick(View view) {
        switch (view.getTag().toString()) {
            case "loadLab":
                addWorkRow(labsCount + 1, 0, labValues, R.id.activity_subject_info_ll_labs_main, getResources().getString(R.string.Lab), Types.lab, null);
                labsCount++;
                labsHeadText.setVisibility(View.VISIBLE);
                break;
            case "loadIHW":
                addWorkRow(ihwCount + 1, 0, ihwValues, R.id.activity_subject_info_ll_ihw_main, getResources().getString(R.string.IHW), Types.ihw, null);
                ihwCount++;
                ihwHeadText.setVisibility(View.VISIBLE);
                break;
            case "loadOther":
                addWorkRow(otherCount + 1, 0, otherValues, R.id.activity_subject_info_ll_other_main, getResources().getString(R.string.other), Types.other, null);
                otherCount++;
                otherHeadText.setVisibility(View.VISIBLE);
                break;
        }
    }

    //обработчик удаление работы
    public void removeOnClick(View view) {
        switch (view.getTag().toString()) {
            case "removeLab":
                if (labsCount > 0) {
                    removeWorkRow(R.id.activity_subject_info_ll_labs_main, labsCount, labValues);
                    --labsCount;
                }
                if (labsCount == 0) labsHeadText.setVisibility(View.GONE);
                break;
            case "removeIHW":
                if (ihwCount > 0) {
                    removeWorkRow(R.id.activity_subject_info_ll_ihw_main, ihwCount, ihwValues);
                    --ihwCount;
                }
                if (ihwCount == 0) ihwHeadText.setVisibility(View.GONE);
                break;
            case "removeOther":
                if (otherCount > 0) {
                    removeWorkRow(R.id.activity_subject_info_ll_other_main, otherCount, otherValues);
                    --otherCount;
                }
                if (otherCount == 0) otherHeadText.setVisibility(View.GONE);
                break;
        }
        setProgress();
    }


*/
    @Override
    public void applyText(String text, Types type, int number, Button name) {
        name.setText(text);
    }

    //создаем кнопку с работой
    private void newWorkRow(LinearLayout linearLayout, String nameText, final int number, int value, final Types type, String workName) {

        //создаем новую полосу название + ценность
        LinearLayout newLine = new LinearLayout(this);
        newLine.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        newLine.setOrientation(LinearLayout.HORIZONTAL);

       /*

        //создаем новое название
        final Button name = new Button(this);
        try {
            if(workName == null || workName.equals(""))  name.setText(nameText + " " + number);
            else name.setText(workName);
        }catch (Exception e){
            System.out.println("Something gone wrong!");
        }

        name.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        name.setGravity(Gravity.CENTER);
        name.setTextSize(TEXT_SIZE);
        name.setTextColor(getColor(R.color.white));
        name.setLayoutParams(new LinearLayout.LayoutParams((int) (190 * this.getResources().getDisplayMetrics().density), LinearLayout.LayoutParams.MATCH_PARENT, 1.0f));
        name.setBackground(getDrawable(R.drawable.lab_style));

        //обработчик добавления название лабы
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SubjectInfoDialogEditText subjectInfoDialogEditText = new SubjectInfoDialogEditText(type, (number-1), name);
                subjectInfoDialogEditText.show(getSupportFragmentManager(), "DialogText");
            }
        };

        name.setOnClickListener(onClickListener);
        newLine.addView(name);

        switch (type){
            case lab: labNames.add(name); break;
            case ihw: ihwNames.add(name);  break;
            case other: otherNames.add(name);  break;
        }



        //обработчик нажатию на кнопку
        AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View container, int position, long id) {
                Drawable drawable = parent.getBackground();
                drawable.setTint(getColor(colors[(int) id]));
                switch (type) {
                    case lab:
                        labValues.set(Integer.parseInt(parent.getTag().toString()) - 1, (int) id);
                        break;
                    case ihw:
                        ihwValues.set(Integer.parseInt(parent.getTag().toString()) - 1, (int) id);
                        break;
                    case other:
                        otherValues.set(Integer.parseInt(parent.getTag().toString()) - 1, (int) id);
                        break;
                }

                setProgress();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        };

        object.setAdapter(adapter);
        object.setId(View.generateViewId());
        object.setTag(number);
        object.setSelection(value);
        object.setOnItemSelectedListener(onItemSelectedListener);
        newLine.addView(object);

        //добавляем в лаяут списка лаб
        linearLayout.addView(newLine);

        //добавляем промежуток между лабами
        addSpace(linearLayout);
        */
    }


    /*
    //режим добавление\удаления работ
    public void refactorOnClick(View view) {
        for (int i = 0; i < tableRows.length; ++i)
            changeState(mGetId(tableRows[i]), isClicked);
        isClicked = !isClicked;
    }
    */

    private void mGetTeacherId(int id){
        if(id!=0) teacherIds.add(id);
    }
}
