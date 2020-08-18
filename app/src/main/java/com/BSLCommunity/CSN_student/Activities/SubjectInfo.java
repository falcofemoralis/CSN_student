package com.BSLCommunity.CSN_student.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.BSLCommunity.CSN_student.Objects.LocalData;
import com.BSLCommunity.CSN_student.Objects.Subjects;
import com.BSLCommunity.CSN_student.Objects.Subjects;
import com.BSLCommunity.CSN_student.Objects.SubjectsInfo;
import com.BSLCommunity.CSN_student.Objects.Teachers;
import com.BSLCommunity.CSN_student.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Locale;

import java.util.concurrent.Callable;

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
    final int TEXT_SIZE = 13; //размер текста
    int[] tableRows = {R.id.activity_subject_info_tr_edit_lab, R.id.activity_subject_info_tr_edit_ihw, R.id.activity_subject_info_tr_edit_other}; //id кнопок добавления\удаления
    int[] workLayouts = {  R.id.activity_subject_info_ll_labs_main, R.id.activity_subject_info_ll_ihw_main, R.id.activity_subject_info_ll_other_main};
    String[] workNames = new String[TYPES_COUNT];

    ArrayList<Integer> teacherIds;  //список учителей для установки
    int subjectId; //id предмета
    int subjectValue = 0; //subjectValue - ценность предмета

    SubjectsInfo.SubjectInfo subjectInfo; //данные робот
    int counts[] = {0,0,0}; //количество робот
    public ArrayList< ArrayList<Integer>> values = new ArrayList<>(); //ценность робот
    public ArrayList<ArrayList<Button>> names = new ArrayList<>(); //названия робот
    TextView[] headTexts = new TextView[TYPES_COUNT];

    public boolean isClicked = false; //состояния нажатия кнопки Refactor



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_info);

        headTexts[0] = (TextView) findViewById(R.id.activity_subject_info_tv_labs_headText);
        headTexts[1] = (TextView) findViewById(R.id.activity_subject_info_tv_ihw_headText);
        headTexts[2] = (TextView) findViewById(R.id.activity_subject_info_tv_other_headText);

        workNames[0] = getResources().getString(R.string.Lab);
        workNames[1] = getResources().getString(R.string.IHW);
        workNames[2] = getResources().getString(R.string.other);

        //получем необходимые объекты
        subjectId = (getIntent().getIntExtra("button_id", 0));
        subjectInfo = SubjectsInfo.getInstance(this).subjectInfo[subjectId];

        for(int i=0; i<TYPES_COUNT;++i)
            counts[i] = subjectInfo.counts[i];

        for(int i=0; i<TYPES_COUNT;++i){
            values.add(new ArrayList<Integer>());
            names.add(new ArrayList<Button>());
        }

        setSubjectName(); //ставим имя предмета
        createValueSpinner(); //создаем спиннер ценностей предмета
        loadData();
        setTeachers();
    }

    //функция вызывается при закрытие активити, в которой идет сохранение данных
    @Override
    protected void onPause() {
       SubjectsInfo instance = SubjectsInfo.getInstance(this);
       instance.saveData(subjectId,subjectValue,TYPES_COUNT,values,names,counts);
        try {
            instance.saveDataToFile(this); //сохраняем в JSON
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, e.toString(),Toast.LENGTH_SHORT).show();
        }
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

        //поле где находятся кнопки в виде (tableRow)
        TableLayout teacherLayout = (TableLayout) findViewById(R.id.activity_subject_info_tl_teachers); //поле где будут кнопки

        //референс tablerow
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
            newTeacherRow(newTeacherText, refBtnText);
            newTeacherRow(newTeacher, refBtn);

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
                String FIO = Teachers.findById(teacherIds.get(i)).FIO;
                JSONObject teacherJSONObject = new JSONObject(Teachers.findById(teacherIds.get(i)).FIO);
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

    //создаем полосу препода
    private void newTeacherRow(Button btn, Button refBtn) {
        btn.setLayoutParams(refBtn.getLayoutParams());
        btn.setBackground(getDrawable(R.drawable.dark_background2));
        btn.setTextColor(getColor(R.color.white));
        btn.setTextSize(TEXT_SIZE);
    }

    //обработчик добавления новой работы
    public void addOnClick(View view) {
        int id = mGetTypeIdByBtnTag(view.getTag().toString()); //получаем id типа
        addWorkRow(counts[id] + 1
                , 0, values.get(id),
                workLayouts[id],
                workNames[id],
                mGetTypeId(id),
                null);

        counts[id]++;
        headTexts[id].setVisibility(View.VISIBLE);
    }

    //обработчик удаление работы
    public void removeOnClick(View view) {
        int id = mGetTypeIdByBtnTag(view.getTag().toString()); //получаем id типа

        if (counts[id] > 0) {
            removeWorkRow(workLayouts[id], counts[id], values.get(id));
            --counts[id];
        }
        if (counts[0] == 0) headTexts[0].setVisibility(View.GONE);
        setProgress();
    }

    //загружаем работы
    public void loadData() {
        //добавляем полосы работ
        for(int i=0; i<TYPES_COUNT;++i){
            for (int j = 0; j < subjectInfo.counts[i];++j)
                addWorkRow(j+1,
                        subjectInfo.values[i][j],
                        values.get(i),
                        workLayouts[i],
                        workNames[i],
                        mGetTypeId(i),
                        subjectInfo.names[i][j]);

            if(counts[i] == 0) headTexts[i].setVisibility(View.GONE);
        }

        //ставим прогресс
        setProgress();
    }

    //добавляем кнопку с работой
    private void addWorkRow(int number, int value,ArrayList<Integer> values, int layoutId, String name, Types type, String workName) {
        values.add(value); //добавляем в лист значений, то что лаба имеет значение спиннера 0 (not passed)
        newWorkRow((LinearLayout) findViewById(layoutId), name, number, value, type, workName);
    }

    @Override
    public void applyText(String text, Types type, int number, Button name) {
        name.setText(text);
    }

    //создаем кнопку с работой
    private void newWorkRow(LinearLayout linearLayout, String nameText, final int number, final int value, final Types type, String workName) {
        //создаем новую полосу название + ценность
        LinearLayout newLine = new LinearLayout(this);
        newLine.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        newLine.setOrientation(LinearLayout.HORIZONTAL);

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
        names.get(type.ordinal()).add(name);

        //создаем новый выбора типа лабы
        Spinner object = new Spinner(this, Spinner.MODE_DIALOG);
        object.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        object.setGravity(Gravity.CENTER);
        object.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f));
        object.setBackground(getDrawable(R.drawable.lab_choose));
        ArrayAdapter adapter = ArrayAdapter.createFromResource(
                this,
                R.array.lab_types,
                R.layout.spinner_registration_layout
        );
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_white);

        //обработчик нажатию на кнопку
        AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View container, int position, long id) {
                Drawable drawable = parent.getBackground();
                drawable.setTint(getColor(colors[(int) id]));
                values.get(type.ordinal()).set(Integer.parseInt(parent.getTag().toString()) - 1, (int) id);
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
    }

    //удаляем работу
    private void removeWorkRow(int id, int count, ArrayList<Integer> countValues) {
        LinearLayout linearLayout = (LinearLayout) findViewById(id);
        linearLayout.removeViewAt((count * 2) - 1); //удаляем пробел (последняя позиция)
        linearLayout.removeViewAt((count * 2) - 2); //удаляем TableRow с кнопками лабы (предпоследняя позиция)
        countValues.remove(count - 1);
    }

    //создаем пробел
    private void addSpace(@NonNull LinearLayout linearLayout) {
        Space space = (Space) findViewById(R.id.activity_subject_info_space);
        Space newSpace = new Space(this);
        newSpace.setLayoutParams(space.getLayoutParams());
        linearLayout.addView(newSpace);
    }

    //устанавливаем прогресс в нижней части экрана
    private void setProgress() {
        Button progress = (Button) findViewById(R.id.activity_subject_info_bt_progress);

        int completed = 0;
        int count = 0;
        for(int i=0; i<TYPES_COUNT;++i)
            for (int j = 0; j < values.get(i).size(); ++j)
                if (values.get(i).get(j) == 6)
                    completed++;

        for(int i=0; i<TYPES_COUNT;++i)
            count += counts[i];

        try {
            progress.setText(Integer.toString(completed * 100 / (count)) + "%");
        } catch (Exception e) {
            System.out.println(e);
            progress.setText("0%");
        }
    }

    //режим добавление\удаления работ
    public void refactorOnClick(View view) {
        for (int i = 0; i < tableRows.length; ++i)
            changeState(mGetTableRowId(tableRows[i]), isClicked);
        isClicked = !isClicked;
    }

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

    private int mGetTypeIdByBtnTag(String type){
        switch (type) {
            case "lab":
                return 0;
            case "ihw":
                return 1;
            case "other":
                return 2;
        }
        return -1;
    }

    private Types mGetTypeId(int id){
        switch (id) {
            case 0:
                return Types.lab;
            case 1:
                return Types.ihw;
            case 2:
                return Types.other;
        }
        return null;
    }
}
