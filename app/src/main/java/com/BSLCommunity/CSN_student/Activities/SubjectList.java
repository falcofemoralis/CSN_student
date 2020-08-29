package com.BSLCommunity.CSN_student.Activities;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.BSLCommunity.CSN_student.Objects.Subjects;
import com.BSLCommunity.CSN_student.Objects.SubjectsInfo;
import com.BSLCommunity.CSN_student.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class SubjectList extends AppCompatActivity {

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
        setSubjectsList();
        setProgress();
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

        // Создание лаяута предмета по шаблону
        LinearLayout subjectLayout = (LinearLayout)LayoutInflater.from(this).inflate(R.layout.inflate_subject_bt, rowSubject, false);

        // Ссылка на кнопку в шаблоне
        Button subjectBt = (Button) subjectLayout.getChildAt(0);

        // Установка названия дисцилпины
        try {
            //получаем имя предмета по локализации
            JSONObject subjectJSONObject = new JSONObject(subject.NameDiscipline);
            subjectBt.setText(subjectJSONObject.getString(Locale.getDefault().getLanguage()));
        } catch (JSONException e) { }

        // Устанавка изображения дисциплины, если оно есть
        BitmapDrawable img = Subjects.getSubjectImage(getApplicationContext(), subject);
        if (img != null) {
            Point size = new Point();
            getWindowManager().getDefaultDisplay().getSize(size);
            int sizeIm = (int) (size.x * 0.5 / 4.2); // Временное решение (не нашел способа растягивать нормально изображение)
            img.setBounds(0, sizeIm / 6, sizeIm, sizeIm + sizeIm / 6);
            subjectBt.setCompoundDrawables(null, img, null, null);
        }


        // Устанавливаем функционал кнопке
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
        rowSubject.addView(subjectLayout); // Добавляем дисциплину
    }

    // Создаем кнопку полной статистики
    protected void createFullStatistics(TableRow rowSubject) {
        // Создание лаяута статистики по шаблону дисциплины
        LinearLayout subjectLayout = (LinearLayout)LayoutInflater.from(this).inflate(R.layout.inflate_subject_bt, rowSubject, false);

        // Ссылка на кнопку в шаблоне
        Button subjectBt = (Button) subjectLayout.getChildAt(0);
        subjectBt.setText("Full statistics");

        // Устанавка изображения дисциплины, если оно есть
        BitmapDrawable img = new BitmapDrawable(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.statistics));;
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        int sizeIm = (int) (size.x * 0.5 / 4.2);
        img.setBounds(0, sizeIm / 6, sizeIm, sizeIm + sizeIm / 6);
        subjectBt.setCompoundDrawables(null, img, null, null);

        // Устанавливаем функционал кнопке
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Здесь должен быть переход на активити полного рейтинга
                return;
            }
        };
        subjectBt.setOnClickListener(onClickListener); // Добавляем функционал кнопке

        // Скрытие строки прогресса (здесь в ней нет необходимости)
        (subjectLayout.getChildAt(1)).setVisibility(View.INVISIBLE);

        rowSubject.addView(subjectLayout); // Добавляем дисциплину
    }

    //создаем список предметов
    public void setSubjectsList() {

        TableRow rowSubject = null;

        int i;
        for (i = 0; i <= Subjects.subjectsList.length; ++i) {
            // В одном ряду может быть лишь 3 кнопки, если уже три созданы, создается следующая колонка
            if (i % 3 == 0) {

                ViewGroup.LayoutParams params = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 2f);
                rowSubject = new TableRow(this);
                rowSubject.setLayoutParams(params);
                rowSubject.setOrientation(TableRow.HORIZONTAL);
                tableSubjects.addView(rowSubject);
            }

            if (i == Subjects.subjectsList.length)
                createFullStatistics(rowSubject);
            else
                createSubject(rowSubject, i);
        }

        // Заполняем пустое пространство
        for (; i < 9; ++i) {

            if (i % 3 == 0) {
                ViewGroup.LayoutParams params = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 2f);
                rowSubject = new TableRow(this);
                rowSubject.setLayoutParams(params);
                rowSubject.setOrientation(TableRow.HORIZONTAL);
                tableSubjects.addView(rowSubject);
            }

            // Создаем невидимый объект (чтобы занять место)
            LinearLayout subjectLayout = (LinearLayout)LayoutInflater.from(this).inflate(R.layout.inflate_subject_bt, rowSubject, false);
            subjectLayout.getChildAt(0).setVisibility(View.INVISIBLE);
            subjectLayout.getChildAt(1).setVisibility(View.INVISIBLE);
            rowSubject.addView(subjectLayout);
        }
    }

    // Устанавливаем прогресс внизу экрана
    public void setProgress() {
        SubjectsInfo subjectsInfo = SubjectsInfo.getInstance(this);

        progresses = new int[subjectsInfo.subjectInfo.length];
        int sumProgress = 0; // Общий прогресс (сумма процентов каждой дисциплины)

        // Подсчет процента прогресса каждой дисциплины
        for (int i = 0; i < subjectsInfo.subjectInfo.length; ++i) {
            progresses[i] = subjectsInfo.subjectInfo[i].calculateProgress();
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

        // Обновление прогресса всех
        updateProgresses();
    }

    // Обновление прогрессов дисциплин
    public void updateProgresses() {
        for (int i = 0; i < tableSubjects.getChildCount(); ++i) {
            TableRow row = (TableRow) tableSubjects.getChildAt(i);

            for (int j = 0; j < row.getChildCount(); ++j) {
                TextView textProgressSubject = (TextView) ((LinearLayout) row.getChildAt(j)).getChildAt(1);
                int index = i * 3 + j;
                if (index != progresses.length)
                    textProgressSubject.setText(progresses[index] + " %");
                else
                    return;
            }
        }
    }
}
