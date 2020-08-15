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
import com.BSLCommunity.CSN_student.Objects.SubjectsInfo;
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
    int labsCount = 0, subjectValue = 0, ihwCount = 0, otherCount = 0; //labsCount - кол-во лаб у предмета, subjectValue - ценность предмета, ihwCount - кол-во ИДЗ
    final int TEXT_SIZE = 13; //размер текста
    public ArrayList<Integer> labValues = new ArrayList<>(); //значения лаб
    public ArrayList<Integer> ihwValues = new ArrayList<>(); //зачения ИДЗ
    public ArrayList<Integer> otherValues = new ArrayList<>(); //зачения заметок
    public boolean isClicked = false; //состояния нажатия кнопки Refactor
    int[] tableRows = {R.id.activity_subject_info_tr_edit_lab, R.id.activity_subject_info_tr_edit_ihw, R.id.activity_subject_info_tr_edit_other}; //id кнопок добавления\удаления

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
        subjectId = (getIntent().getIntExtra("button_id", 0) - 1);

        setSubjectName(); //ставим имя предмета
        createValueSpinner(); //создаем спиннер ценностей предмета

        //загружаем информацию про кол-во работ
        labsCount = subjectsInfo.subjectInfo[subjectId].labsCount;
        ihwCount = subjectsInfo.subjectInfo[subjectId].ihwCount;
        otherCount = subjectsInfo.subjectInfo[subjectId].otherCount;
        loadData();

        //ставим преподавателей
        getTeachers(this, new Callable<Void>() {
            @Override
            public Void call() {
                setTeachers();
                return null;
            }
        });
    }

    //функция вызывается при закрытие активити, в которой идет сохранение данных
    @Override
    protected void onPause() {
        subjectsInfo.saveSubjectValue(subjectId, subjectValue); //сохраням ценность предмета
        subjectsInfo.saveCount(subjectId, labsCount, ihwCount, otherCount);  //сохраням кол-во
        subjectsInfo.saveValues(subjectId, labValues, labsCount, ihwValues, ihwCount, otherValues, otherCount); //сохраням типы
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
        teacherIds.add(Subjects.subjectsList[subjectId].Code_Lector);
        teacherIds.add(Subjects.subjectsList[subjectId].Code_Practice);
        teacherIds.add(Subjects.subjectsList[subjectId].Code_Assistant);

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

    //создаем полосу препода
    private void newTeacherRow(Button btn, Button refBtn) {
        btn.setLayoutParams(refBtn.getLayoutParams());
        btn.setBackground(getDrawable(R.drawable.dark_background2));
        btn.setTextColor(getColor(R.color.white));
        btn.setTextSize(TEXT_SIZE);
    }

    //обработчик добавления новой работы
    public void addOnClick(View view) {
        switch (view.getTag().toString()) {
            case "loadLab":
                addWorkRow(labsCount + 1, 0, labValues, R.id.activity_subject_info_ll_labs_main, getResources().getString(R.string.Lab), Types.lab);
                labsCount++;
                break;
            case "loadIHW":
                addWorkRow(labsCount + 1, 0, ihwValues, R.id.activity_subject_info_ll_ihw_main, getResources().getString(R.string.IHW), Types.ihw);
                ihwCount++;
                break;
            case "loadOther":
                addWorkRow(labsCount + 1, 0, otherValues, R.id.activity_subject_info_ll_other_main, getResources().getString(R.string.other), Types.other);
                otherCount++;
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
                break;
            case "removeIHW":
                if (ihwCount > 0) {
                    removeWorkRow(R.id.activity_subject_info_ll_ihw_main, ihwCount, ihwValues);
                    --ihwCount;
                }
                break;
            case "removeOther":
                if (otherCount > 0) {
                    removeWorkRow(R.id.activity_subject_info_ll_other_main, otherCount, otherValues);
                    --otherCount;
                }
                break;
        }
        setProgress();
    }

    //загружаем работы
    public void loadData() {
        //добавляем полосы работ (лаб, идз и прочего)
        for (int i = 0; i < labsCount; ++i)
            addWorkRow(i + 1, subjectsInfo.subjectInfo[subjectId].labValue[i], labValues, R.id.activity_subject_info_ll_labs_main, getResources().getString(R.string.Lab), Types.lab);
        for (int i = 0; i < ihwCount; ++i)
            addWorkRow(i + 1, subjectsInfo.subjectInfo[subjectId].ihwValue[i], ihwValues, R.id.activity_subject_info_ll_ihw_main, getResources().getString(R.string.IHW), Types.ihw);
        for (int i = 0; i < otherCount; ++i)
            addWorkRow(i + 1, subjectsInfo.subjectInfo[subjectId].otherValue[i], otherValues, R.id.activity_subject_info_ll_other_main, getResources().getString(R.string.other), Types.other);

        //ставим прогресс
        setProgress();
    }

    //добавляем кнопку с работой
    private void addWorkRow(int number, int value, ArrayList<Integer> count, int layoutId, String name, Types type) {
        count.add(value); //добавляем в лист значений, то что лаба имеет значение спиннера 0 (not passed)
        newWorkRow((LinearLayout) findViewById(layoutId), name, number, value, type);
    }

    //создаем кнопку с работой
    private void newWorkRow(LinearLayout linearLayout, String NameText, int number, int value, final Types type) {
        //создаем новую полосу название + ценность
        LinearLayout newLine = new LinearLayout(this);
        newLine.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        newLine.setOrientation(LinearLayout.HORIZONTAL);

        //создаем новое название
        TextView name = new TextView(this);
        name.setText(NameText + " " + number);
        name.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        name.setTextSize(TEXT_SIZE);
        name.setTextColor(getColor(R.color.white));
        name.setGravity(Gravity.CENTER);
        name.setLayoutParams(new LinearLayout.LayoutParams((int) (190 * this.getResources().getDisplayMetrics().density), LinearLayout.LayoutParams.MATCH_PARENT, 1.0f));
        name.setBackground(getDrawable(R.drawable.lab_style));
        newLine.addView(name);

        //создаем новый выбора типа лабы
        Spinner object = new Spinner(this, Spinner.MODE_DIALOG);
        object.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        object.setGravity(Gravity.CENTER);
        object.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
        object.setBackground(getDrawable(R.drawable.lab_choose));
        ArrayAdapter adapter = ArrayAdapter.createFromResource(
                this,
                R.array.lab_types,
                R.layout.spinner_registration_layout
        );
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_white);

        //обработчик нажатию на кнопку
        AdapterView.OnItemSelectedListener onClickListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View container, int position, long id) {
                Drawable drawable = parent.getBackground();
                drawable.setTint(getColor(colors[(int) id]));

                if (type == Types.lab){
                    System.out.println("!!!!!!!!!!!!!!!!!="+Integer.parseInt(parent.getTag().toString()) );
                    System.out.println("!!!!!!!!!!!!!!!!!="+id );
                    System.out.println("!!!!!!!!!!!!!!!!!="+labValues.size());

                    labValues.set(Integer.parseInt(parent.getTag().toString()) - 1, (int) id);
                }
                else if (type == Types.ihw){
                    System.out.println("!!!!!!!!!!!!!!!!!="+Integer.parseInt(parent.getTag().toString()) );
                    System.out.println("!!!!!!!!!!!!!!!!!="+id );
                    System.out.println("!!!!!!!!!!!!!!!!!="+ihwValues.size());

                    ihwValues.set(Integer.parseInt(parent.getTag().toString()) - 1, (int) id);

                }
                else if (type == Types.other){
                    System.out.println("!!!!!!!!!!!!!!!!!="+Integer.parseInt(parent.getTag().toString()) );
                    System.out.println("!!!!!!!!!!!!!!!!!="+id );
                    System.out.println("!!!!!!!!!!!!!!!!!="+otherValues.size());

                    otherValues.set(Integer.parseInt(parent.getTag().toString()) - 1, (int) id);
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
        object.setOnItemSelectedListener(onClickListener);
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

    //режим добавление\удаления работ
    public void refactorOnClick(View view) {
        for (int i = 0; i < tableRows.length; ++i)
            changeState(mGetId(tableRows[i]), isClicked);
        isClicked = !isClicked;
    }

    //меняем режим  добавление\удаления работ
    private void changeState(TableRow tableRow, boolean state) {
        if (state) tableRow.setVisibility(View.GONE);
        else tableRow.setVisibility(View.VISIBLE);
    }

    //получение view TableRow по id
    private TableRow mGetId(int id) {
        return findViewById(id);
    }
}
