package com.BSLCommunity.CSN_student.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.BSLCommunity.CSN_student.Objects.Subjects;
import com.BSLCommunity.CSN_student.Objects.Teachers;
import com.BSLCommunity.CSN_student.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.Callable;

import static com.BSLCommunity.CSN_student.Objects.Teachers.getTeachers;

public class SubjectInfo extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    int[] colors = { //цвета кнопок выбора
            R.color.not_passed,
            R.color.in_process,
            R.color.done_without_report,
            R.color.done_with_report,
            R.color.waiting_acceptation,
            R.color.passed_without_report,
            R.color.passed_with_report};
    ArrayList<Integer> teacherIds;  //список учителей для установки
    int labsCount = 0, subjectValue = 0; //labsCount - кол-во лаб у предмета, subjectValue - ценность предмета
    final int TEXT_SIZE = 13; //размер текста
    public ArrayList<Integer> labValues = new ArrayList<>(); //зачения лаб
    public boolean isClicked = false; //состояния нажатия кнопки Refactor
    int[] tableRows = {R.id.activity_subject_info_tr_edit_lab, R.id.activity_subject_info_tr_edit_ihw, R.id.activity_subject_info_tr_edit_other}; //id кнопок добавления\удаления

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_info);
        createValueSpinner();
        setSubjectName();

        getTeachers(this, new Callable<Void>() {
            @Override
            public Void call() {
                setTeachers();
                return null;
            }
        });

        try {
            labsCount = Subjects.getInstance(this).getLabCount(getIntent().getIntExtra("button_id", 0));
            loadLabs();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        int subjectId = getIntent().getIntExtra("button_id", 0);
        Subjects subjects = Subjects.getInstance(this);
        subjects.saveLabCount(this, subjectId, labsCount);  //сохраням кол-во лаб
        subjects.saveSubjectValue(this, subjectId, subjectValue); //сохраням ценность предмета
        subjects.saveLabValue(this, subjectId, labValues, labsCount); //сохраням тип лабы
        subjects.saveSubject(this); //сохраняем в JSON
        super.onPause();
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
            valueSpinner.setSelection(Subjects.getInstance(this).getSubjectValue(getIntent().getIntExtra("button_id", 0)));
        } catch (Exception e) {
            valueSpinner.setSelection(subjectValue);
        }

        valueSpinner.setOnItemSelectedListener(this);
    }

    //выбор элемента на спинере
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.activity_subject_info_sp_values) {
            subjectValue = (int) id;
        } else {
            Drawable drawable = parent.getBackground();
            drawable.setTint(getColor(colors[(int) id]));
            labValues.set(Integer.parseInt(parent.getTag().toString()) - 1, (int) id);
            setProgress();
        }
    }

    //нужен для реализации интерфейса AdapterView.OnItemSelectedListener
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    //устанавлиаем название предмета
    public void setSubjectName() {
        int subjectId = getIntent().getIntExtra("button_id", 0);

        Button subjectNameBtn = (Button) findViewById(R.id.activity_subject_info_bt_subjectName);
        try {
            JSONObject subjectJSONObject = new JSONObject(Subjects.subjectsList[subjectId - 1].NameDiscipline);
            subjectNameBtn.setText(subjectJSONObject.getString(Locale.getDefault().getLanguage()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //устанавливаем преподователей
    public void setTeachers() {
        int subjectId = getIntent().getIntExtra("button_id", 0);

        //получаем id учителя
        teacherIds = new ArrayList<>(); //список учителей для установки
        getTeacherId(Subjects.subjectsList[subjectId - 1].Code_Lector);
        getTeacherId(Subjects.subjectsList[subjectId - 1].Code_Practice);
        getTeacherId(Subjects.subjectsList[subjectId - 1].Code_Assistant);

        //поле где находятся кнопки в виде (tableRow)
        TableLayout teacherLayout = (TableLayout) findViewById(R.id.activity_subject_info_tl_teachers); //поле где будут кнопки

        //референс tablerow (1+1)
        TableRow refRow = (TableRow) findViewById(R.id.activity_subject_info_tr_ref);

        //референсы кнопок
        Button refBtnText = (Button) findViewById(R.id.activity_subject_info_bt_ref_text);
        Button refBtn = (Button) findViewById(R.id.activity_subject_info_bt_ref);

        //выключаем референсы
        refRow.setVisibility(View.GONE);
        refBtnText.setVisibility(View.GONE);
        refBtn.setVisibility(View.GONE);

        for (int i = 0; i < teacherIds.size(); ++i) {
            //создаем еще новый TableRow
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(refRow.getLayoutParams());

            //новые конпки с параметрами
            Button newTeacherText = new Button(this);
            Button newTeacher = new Button(this);

            //устанавливаем необходимые параметры
            addTeacher(newTeacherText, refBtnText);
            addTeacher(newTeacher, refBtn);

            //устанавливаем тип препода
            switch (i) {
                case 0:
                    newTeacherText.setText(getString(R.string.lector));
                    break;
                case 1:
                    newTeacherText.setText(getString(R.string.practice));
                    break;
                case 2:
                    newTeacherText.setText(getString(R.string.assistant));
                    break;
            }

            //устанавливаем имя препода
            try {
                JSONObject teacherJSONObject = new JSONObject(Teachers.teachersList.get(teacherIds.get(i)));
                newTeacher.setText(teacherJSONObject.getString(Locale.getDefault().getLanguage()));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //добавляем в лаяут
            tableRow.addView(newTeacherText);
            tableRow.addView(newTeacher);
            teacherLayout.addView(tableRow);

            //добавляем пробел
            addSpace(teacherLayout);
        }
    }

    //получение id преподавателя
    private void getTeacherId(int id) {
        try {
            teacherIds.add(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //создаем кнопку с учителем
    private void addTeacher(Button btn, Button refBtn) {
        btn.setLayoutParams(refBtn.getLayoutParams());
        btn.setBackground(getDrawable(R.drawable.dark_background2));
        btn.setTextColor(getColor(R.color.white));
        btn.setTextSize(TEXT_SIZE);
    }

    //обработчик добавления новой лабы
    public void addLabOnClick(View view) {
        addLab(labsCount + 1, 0);
        labsCount++;
    }

    //удаление лабы
    public void removeLabOnClick(View view) {
        if (labsCount > 0) {
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.activity_subject_info_ll_labs_main);
            linearLayout.removeViewAt((labsCount * 2) - 1); //удаляем пробел (последняя позиция)
            linearLayout.removeViewAt((labsCount * 2) - 2); //удаляем TableRow с кнопками лабы (предпоследняя позиция)
            labValues.remove(labsCount - 1);
            labsCount--;
        }
        setProgress();
    }

    //загружаем лабы
    public void loadLabs() {
        int SubjectId = getIntent().getIntExtra("button_id", 0);
        for (int i = 1; i <= labsCount; ++i)
            addLab(i, Subjects.getInstance(this).subjectInfo[SubjectId - 1].labValue[i - 1]);
        setProgress();
    }

    //создаем кнопку с лабой
    private void addLab(int number, int value) {
        labValues.add(value); //добавляем в лист значений, то что лаба имеет значение спиннера 0 (not passed)
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.activity_subject_info_ll_labs_main);

        //создаем новую полосу название лабы + тип лабы
        LinearLayout newLine = new LinearLayout(this);
        newLine.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        newLine.setOrientation(LinearLayout.HORIZONTAL);

        //создаем новое название лабы
        TextView labName = new TextView(this);
        labName.setText(getResources().getString(R.string.Lab) + " " + number);
        labName.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        labName.setTextSize(TEXT_SIZE);
        labName.setTextColor(getColor(R.color.white));
        labName.setGravity(Gravity.CENTER);
        labName.setLayoutParams(new LinearLayout.LayoutParams((int) (190 * this.getResources().getDisplayMetrics().density), LinearLayout.LayoutParams.MATCH_PARENT, 1.0f));
        labName.setBackground(getDrawable(R.drawable.lab_style));
        newLine.addView(labName);

        //создаем новый выбора типа лабы
        Spinner lab = new Spinner(this, Spinner.MODE_DIALOG);
        lab.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        lab.setGravity(Gravity.CENTER);
        lab.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
        lab.setBackground(getDrawable(R.drawable.lab_choose));
        ArrayAdapter adapter = ArrayAdapter.createFromResource(
                this,
                R.array.lab_types,
                R.layout.spinner_registration_layout
        );
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_white);
        lab.setAdapter(adapter);
        lab.setId(View.generateViewId());
        lab.setTag(number);
        lab.setSelection(value);
        lab.setOnItemSelectedListener(this);
        newLine.addView(lab);

        //добавляем в лаяут списка лаб
        linearLayout.addView(newLine);

        //добавляем промежуток между лабами
        addSpace(linearLayout);
    }

    //создаем пробел
    private void addSpace(@NonNull LinearLayout linearLayout) {
        Space space = (Space) findViewById(R.id.activity_subject_info_space);
        Space newSpace = new Space(this);
        newSpace.setLayoutParams(space.getLayoutParams());
        linearLayout.addView(newSpace);
    }

    //устанавливаем прогресс в нижней части экрана
    private void setProgress(){
        Button progress = (Button) findViewById(R.id.activity_subject_info_bt_progress);

        int completed = 0;
        for(int i=0;i<labValues.size();++i)
            if(labValues.get(i)==6)
                completed++;

        try {
            progress.setText(Integer.toString(completed * 100 / (labsCount)) + "%");
        }catch (Exception e){
            System.out.println(e);
            progress.setText("0%");
        }
    }

    public void refactorOnClick(View view) {
        for (int i = 0; i < tableRows.length; ++i)
            changeState(mGetId(tableRows[i]), isClicked);
        isClicked = !isClicked;
    }

    private void changeState(TableRow tableRow, boolean state) {
        if (state) tableRow.setVisibility(View.GONE);
        else tableRow.setVisibility(View.VISIBLE);
    }

    private TableRow mGetId(int id){
        return findViewById(id);
    }
}
