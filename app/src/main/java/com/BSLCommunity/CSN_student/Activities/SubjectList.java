package com.BSLCommunity.CSN_student.Activities;

import android.content.Intent;
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
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.BSLCommunity.CSN_student.Objects.Subjects;
import com.BSLCommunity.CSN_student.Objects.SubjectsInfo;
import com.BSLCommunity.CSN_student.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.concurrent.Callable;

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

    // Лаяут всех дисциплин
    LinearLayout tableSubjects;

    // Счетчик установленных дисциплин
    int countSetSubjects = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_list);

        tableSubjects = findViewById(R.id.activity_subject_list_ll_table_subjects);

        //получаем список групп
        Subjects.getSubjectsList(this, new Callable<Void>() {
            @Override
            public Void call() {
                setSubjectsList();
                setProgress();
                return null;
            }
        });
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


    //создаем список предметов
    public void setSubjectsList() {

        LinearLayout rowSubject = null;

        for (countSetSubjects = 0; countSetSubjects < Subjects.subjectsList.length; ++countSetSubjects) {
            // В одном ряду может быть лишь 3 кнопки, если уже три созданы, создается следующая колонка
            if (countSetSubjects % 3 == 0) {
                ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 2f);
                rowSubject = new LinearLayout(this);
                rowSubject.setOrientation(LinearLayout.HORIZONTAL);
                this.addContentView(rowSubject, params);
            }

            // Создаем разделитель
            Space space = new Space(this);
            space.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.1f));
            rowSubject.addView(space); // Добавляем разделитель

            // Создаем предмет

            // Создание лаяута для кнопки и текстовых полей о статистике по предмету
            LinearLayout subjectLayout = new LinearLayout(this);
            ViewGroup.LayoutParams paramsLayout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
            subjectLayout.setOrientation(LinearLayout.VERTICAL);
            subjectLayout.setLayoutParams(paramsLayout);

            // Создание кнопки предмета
            Button subjectBt = new Button(this);
            ViewGroup.LayoutParams paramsBt = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 0.2f);
            subjectBt.setLayoutParams(paramsBt);// Устанавливаем параметры макета

            try {
                //получаем имя предмета по локализации
                JSONObject subjectJSONObject = new JSONObject(Subjects.subjectsList[countSetSubjects].NameDiscipline);
                subjectBt.setText(subjectJSONObject.getString(Locale.getDefault().getLanguage())); // Устаналиваем название дисциплины
            } catch (JSONException e) { }
            subjectBt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12); // Устанавливаем размер текста
            subjectBt.setBackgroundResource(R.drawable.ic_subject_list); // Устанавливаем фон для кнопки

            subjectLayout.addView(subjectBt); // Добавляем кнопку
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

            // Создание текстового поля прогресса
            TextView progressText = new TextView(this);
            ViewGroup.LayoutParams paramsText = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 0.9f);
            subjectBt.setBackgroundResource(R.drawable.subject_progress_subject_list); // Устанавливаем фон для текста
            progressText.setLayoutParams(paramsText); // Устанавливаем параметры макета
            progressText.setGravity(Gravity.CENTER); // Устанавливаем позицию текста в центре кнопки
            progressText.setTextColor( ContextCompat.getColor(this, R.color.white)); // Устанавливаем цвет текста
            progressText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);  // Устанавливаем размер текста
            progressText.setText("23 %");

            subjectLayout.addView(progressText); // Добавляем текст

            rowSubject.addView(subjectLayout); // Добавляем дисциплину
        }

        /*
        //достаем параметры референсной кнопки
        ViewGroup.LayoutParams refParams = refBtn.getLayoutParams();

        //устанавливаем кнопки  предметов
        String subjectName = "";
        for (int i = 0; i < Subjects.subjectsList.length; ++i) {
            try {
                //получаем имя предмета по локализации
                JSONObject subjectJSONObject = new JSONObject(Subjects.subjectsList[i].NameDiscipline);
                subjectName = subjectJSONObject.getString(Locale.getDefault().getLanguage());
            } catch (JSONException e) {
            }

            final int subjectId = IdGenerator.getId();
            //обработчик нажатию на кнопку
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

            LinearLayout layout = (LinearLayout) findViewById(R.id.activity_subject_list_ll_main); //поле где будут кнопки
            Button subjectBtn = new Button(this); //новая кнопка

            //даем параметры кнопки
            subjectBtn.setLayoutParams(refParams);
            subjectBtn.setBackground(getDrawable(R.drawable.button_style));
            subjectBtn.setTextColor(getColor(R.color.white));
            subjectBtn.setTextSize(12);
            subjectBtn.setOnClickListener(onClickListener);
            subjectBtn.setText(subjectName);
            subjectBtn.setId(View.generateViewId());

            //добавляем кнопку на поле
            layout.addView(subjectBtn);
        }

        if (Subjects.subjectsList.length == 0) {
            TextView noSubjects = (TextView) findViewById(R.id.activity_subject_list_tv_noSubjects);
            noSubjects.setVisibility(View.VISIBLE);
        }*/
    }

    //устанавливаем прогресс внизу экрана
    public void setProgress() {
        SubjectsInfo subjectsInfo = SubjectsInfo.getInstance(this);

        int allLabsCount = 0, allCompleted = 0;
        for (int i = 0; i < subjectsInfo.subjectInfo.length; ++i) {
            allLabsCount += (subjectsInfo.subjectInfo[i].labsCount + subjectsInfo.subjectInfo[i].ihwCount + subjectsInfo.subjectInfo[i].otherCount);

            for (int j = 0; j < subjectsInfo.subjectInfo[i].labsCount; ++j)
                if (subjectsInfo.subjectInfo[i].labValue[j] == 6) allCompleted++;

            for (int j = 0; j < subjectsInfo.subjectInfo[i].ihwCount; ++j)
                if (subjectsInfo.subjectInfo[i].ihwValue[j] == 6) allCompleted++;

            for (int j = 0; j < subjectsInfo.subjectInfo[i].otherCount; ++j)
                if (subjectsInfo.subjectInfo[i].otherValue[j] == 6) allCompleted++;
        }


        Button progress = (Button) findViewById(R.id.activity_subject_list_bt_progress);

        try {
            progress.setText(Integer.toString(allCompleted * 100 / (allLabsCount)) + "%");
        } catch (Exception e) {
            System.out.println(e);
            progress.setText("0%");
        }
    }
}
