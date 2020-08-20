package com.BSLCommunity.CSN_student.Activities;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.BSLCommunity.CSN_student.Objects.Subjects;
import com.BSLCommunity.CSN_student.Objects.SubjectsInfo;
import com.BSLCommunity.CSN_student.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class SubjectList extends AppCompatActivity {
    Button refBtn;
    Boolean shouldExecuteOnResume = false;
    static class IdGenerator {
        static int n = 0;

        static int getId() {
            return n++;
        }

        static void reset() {
            n = 0;
        }
    }

    TableLayout tableSubjects; // Лаяут всех дисциплин
    int[] progresses; // Прогресс для каждого предмета


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_list);

        tableSubjects = findViewById(R.id.activity_subject_list_ll_table_subjects);
        setProgress();
        setSubjectsList();
    }

    @Override
    protected void onResume() {
        if (shouldExecuteOnResume) setProgress();
        else shouldExecuteOnResume = true;
        super.onResume();
    }

    @Override
    protected void onPause() {
        IdGenerator.reset();
        super.onPause();
    }

    // Создаем кнопку одной дисциплины
    protected void createSubject(TableRow rowSubject, int numberSubject) {

        Subjects.SubjectsList subject = Subjects.subjectsList[numberSubject];

        // Создание лаяута для кнопки и текстовых полей о статистике по предмету
        LinearLayout subjectLayout = new LinearLayout(this);
        ViewGroup.LayoutParams paramsLayout = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
        subjectLayout.setOrientation(TableRow.VERTICAL);
        subjectLayout.setLayoutParams(paramsLayout);

        // Создание кнопки предмета
        Button subjectBt = new Button(this);
        ViewGroup.LayoutParams paramsBt = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 0.2f);
        subjectBt.setLayoutParams(paramsBt);// Устанавливаем параметры макета

        try {
            //получаем имя предмета по локализации
            JSONObject subjectJSONObject = new JSONObject(subject.NameDiscipline);
            subjectBt.setText(subjectJSONObject.getString(Locale.getDefault().getLanguage())); // Устаналиваем название дисциплины
        } catch (JSONException e) { }
        subjectBt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12); // Устанавливаем размер текста
        subjectBt.setMaxEms(1); // Для того чтобы текст не растягивал кнопки
        subjectBt.setGravity(Gravity.BOTTOM);
        subjectBt.setGravity(Gravity.CENTER);


        BitmapDrawable img = Subjects.getSubjectImage(getApplicationContext(), subject);
        // Устанавливаем изображение дисциплины, если оно есть
        if (img != null) {
            Point size = new Point();
            getWindowManager().getDefaultDisplay().getSize(size);
            int sizeIm = (int) (size.x * 0.5 / 4.2);
            img.setBounds(0, 0, sizeIm, sizeIm);
            subjectBt.setCompoundDrawables(null, img, null, null);
        }

        subjectBt.setBackgroundResource(R.drawable.ic_subject_list_v2); // Устанавливаем фон для кнопки

        final int subjectId = IdGenerator.getId();
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation click = AnimationUtils.loadAnimation(SubjectList.this, R.anim.btn_click);
                view.startAnimation(click);
                Intent intent = new Intent(SubjectList.this, SubjectInfo.class);
                intent.putExtra("button_id", subjectId);
                startActivity(intent);
            }
        };
        subjectBt.setOnClickListener(onClickListener); // Добавляем функционал кнопке
        subjectLayout.addView(subjectBt); // Добавляем кнопку


        // Создание текстового поля прогресса
        TextView progressText = new TextView(this);
        ViewGroup.LayoutParams paramsText = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 0.9f);
        progressText.setBackgroundResource(R.drawable.subject_progress_subject_list_v2); // Устанавливаем фон для текста
        progressText.setLayoutParams(paramsText); // Устанавливаем параметры макета
        progressText.setGravity(Gravity.CENTER); // Устанавливаем позицию текста в центре кнопки
        progressText.setTextColor( ContextCompat.getColor(this, R.color.white)); // Устанавливаем цвет текста
        progressText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);  // Устанавливаем размер текста
        progressText.setText(progresses[numberSubject] + " %");

        subjectLayout.addView(progressText); // Добавляем текст

        rowSubject.addView(subjectLayout); // Добавляем дисциплину
    }

    // Создаем кнопку полной статистики
    protected void createFullStatistics(TableRow rowSubject) {
        // Создание лаяута для кнопки и текстовых полей о статистике по предмету
        LinearLayout subjectLayout = new LinearLayout(this);
        ViewGroup.LayoutParams paramsLayout = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
        subjectLayout.setOrientation(TableRow.VERTICAL);
        subjectLayout.setLayoutParams(paramsLayout);

        // Создание кнопки статистики
        Button subjectBt = new Button(this);
        ViewGroup.LayoutParams paramsBt = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 0.2f);
        subjectBt.setLayoutParams(paramsBt);// Устанавливаем параметры макета
        subjectBt.setText("Full statistics");
        subjectBt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12); // Устанавливаем размер текста
        subjectBt.setMaxEms(1); // Для того чтобы текст не растягивал кнопки
        subjectBt.setBackgroundResource(R.drawable.ic_subject_list_v2); // Устанавливаем фон для кнопки


        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Здесь должен быть переход на активити полного рейтинга
                return;
            }
        };
        subjectBt.setOnClickListener(onClickListener); // Добавляем функционал кнопке
        subjectLayout.addView(subjectBt); // Добавляем кнопку

        // Коррекция кнопки
        subjectLayout.addView(new Space(this), new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 0.9f));;

        rowSubject.addView(subjectLayout); // Добавляем дисциплину
    }

    //создаем список предметов
    public void setSubjectsList() {

        TableRow rowSubject = null;

        int i;
        for (i = 0; i <= Subjects.subjectsList.length; ++i) {
            // В одном ряду может быть лишь 3 кнопки, если уже три созданы, создается следующая колонка
            if (i % 3 == 0) {

                // Добавляем последний разделитель между краем экрана и крайней правой кнопкой
                if (rowSubject != null) {
                    // Создаем разделители
                    rowSubject.addView(new Space(this), new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.3f));
                    tableSubjects.addView(new Space(this), new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.3f));
                }

                ViewGroup.LayoutParams params = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 2f);
                rowSubject = new TableRow(this);
                rowSubject.setOrientation(TableRow.HORIZONTAL);
                tableSubjects.addView(rowSubject, params);
            }

            // Создаем разделитель
            rowSubject.addView(new Space(this), new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.3f));

            if (i == Subjects.subjectsList.length)
                createFullStatistics(rowSubject);
            else
                createSubject(rowSubject, i);
        }

        // Заполняем пустое пространство
        for (; i < 9; ++i) {

            // Создаем разделитель
            rowSubject.addView(new Space(this), new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.3f));

            // Создаем разделитель (место где должна была быть кнопка)
            rowSubject.addView(new Space(this), new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        }

        // Создаем последний разделитель справа
        rowSubject.addView(new Space(this), new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.3f));
    }

    //устанавливаем прогресс внизу экрана
    public void setProgress() {
        SubjectsInfo subjectsInfo = SubjectsInfo.getInstance(this);

        progresses = new int[subjectsInfo.subjectInfo.length];
        int sumProgress = 0; // Общий прогресс в процентах


        for (int i = 0; i < subjectsInfo.subjectInfo.length; ++i) {
            int subjectComplete = 0, subjectAllWork;


            // Считаем сколько он выполнил лабораторных, ИДЗ, других дел
            for (int j = 0; j < subjectsInfo.subjectInfo[i].labsCount; ++j)
                if (subjectsInfo.subjectInfo[i].labValue[j] == 6) subjectComplete++;

            for (int j = 0; j < subjectsInfo.subjectInfo[i].ihwCount; ++j)
                if (subjectsInfo.subjectInfo[i].ihwValue[j] == 6) subjectComplete++;

            for (int j = 0; j < subjectsInfo.subjectInfo[i].otherCount; ++j)
                if (subjectsInfo.subjectInfo[i].otherValue[j] == 6) subjectComplete++;

            // Считаем сколько всего предстоит работы пользователю
            subjectAllWork = (subjectsInfo.subjectInfo[i].labsCount + subjectsInfo.subjectInfo[i].ihwCount + subjectsInfo.subjectInfo[i].otherCount);

            // Считаем процент выполненной работы студент за дисциплину
            progresses[i] = subjectComplete != 0 ? subjectComplete * 100 / (subjectAllWork) : 0;
            sumProgress += progresses[i];
        }

        // Подсчитываем общий процент и выводим на экран
        Button progress = (Button) findViewById(R.id.activity_subject_list_bt_progress);

        try {
            progress.setText(sumProgress / progresses.length + "%");
        } catch (Exception e) {
            System.out.println(e.toString());
            progress.setText("0%");
        }
    }
}
