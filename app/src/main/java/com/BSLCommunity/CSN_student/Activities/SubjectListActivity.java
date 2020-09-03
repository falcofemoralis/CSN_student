package com.BSLCommunity.CSN_student.Activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.BSLCommunity.CSN_student.Managers.AnimationManager;
import com.BSLCommunity.CSN_student.Objects.Subjects;
import com.BSLCommunity.CSN_student.Objects.SubjectsInfo;
import com.BSLCommunity.CSN_student.Objects.User;
import com.BSLCommunity.CSN_student.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class SubjectListActivity extends AppCompatActivity {
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
    Boolean can_click; //нажата кнопка

    class SubjectDrawable {
        Button button;
        TextView textProgress;
        ProgressBar progressBar;

        // Создаем кнопку одной дисциплины
        protected void createSubject(TableRow rowSubject, int numberSubject) {

            Subjects.SubjectsList subject = Subjects.subjectsList[numberSubject];

            // Инициализация всех деталей группы view элементов дисциплины
            LinearLayout subjectLayout = (LinearLayout)LayoutInflater.from(getApplicationContext()).inflate(R.layout.inflate_subject_bt, rowSubject, false);
            button = (Button) ((RelativeLayout)subjectLayout.getChildAt(0)).getChildAt(0);
            progressBar = (ProgressBar) ((RelativeLayout)subjectLayout.getChildAt(0)).getChildAt(1);
            textProgress = (TextView) subjectLayout.getChildAt(1);

            // Установка названия дисцилпины
            try {
                //получаем имя предмета по локализации
                JSONObject subjectJSONObject = new JSONObject(subject.NameDiscipline);
                button.setText(subjectJSONObject.getString(Locale.getDefault().getLanguage()));
            } catch (JSONException e) {}

            // Устанавливаем функционал кнопке
            final int subjectId = IdGenerator.getId();
            View.OnTouchListener onTouchListener = new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    TransitionDrawable transitionDrawable = (TransitionDrawable) view.getBackground();
                    Intent intent = null;
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN && can_click) {
                        transitionDrawable.startTransition(150);
                        view.startAnimation(AnimationUtils.loadAnimation(SubjectListActivity.this, R.anim.btn_pressed));
                    }
                    else if (motionEvent.getAction() == MotionEvent.ACTION_UP && can_click) {
                        intent = new Intent(SubjectListActivity.this, SubjectInfoActivity.class);
                        intent.putExtra("button_id", subjectId);

                        can_click = false;
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SubjectListActivity.this);
                        startActivity(intent, options.toBundle());

                        transitionDrawable.reverseTransition(150);
                        view.startAnimation(AnimationUtils.loadAnimation(SubjectListActivity.this, R.anim.btn_unpressed));

                    }
                    return true;
                }
            };

            button.setOnTouchListener(onTouchListener); // Добавляем функционал кнопке
            rowSubject.addView(subjectLayout); // Добавляем дисциплину
        }

        // Установка изображения на кнопку
        protected void setImg(BitmapDrawable img) {
            // Устанавка изображения дисциплины
            Point size = new Point();
            getWindowManager().getDefaultDisplay().getSize(size);
            int sizeIm = (int) (size.x * 0.5 / 4.2); // Временное решение (не нашел способа растягивать нормально изображение)
            img.setBounds(0, sizeIm / 12, sizeIm, sizeIm + sizeIm / 12);
            button.setCompoundDrawables(null, img, null, null);
        }
    }
    SubjectDrawable[] subjectDrawables; // Дисциплины

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnimationManager.setAnimation(getWindow(), this);
        setContentView(R.layout.activity_subject_list);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        TextView courseTextView = (TextView) findViewById(R.id.activity_subject_list_tv_course);
        courseTextView.setText(User.getInstance().course + " " + courseTextView.getText());
        tableSubjects = findViewById(R.id.activity_subject_list_ll_table_subjects);
        setSubjectsList();
        setProgress();
    }

    @Override
    protected void onResume() {
        can_click = true;
        if (shouldExecuteOnResume) setProgress();
        else shouldExecuteOnResume = true;
        super.onResume();
    }

    @Override
    protected void onPause() {
        IdGenerator.reset();
        super.onPause();
    }

    // Создаем кнопку полной статистики
    protected void createFullStatistics(TableRow rowSubject) {
        // Создание лаяута статистики по шаблону дисциплины
        LinearLayout subjectLayout = (LinearLayout)LayoutInflater.from(this).inflate(R.layout.inflate_subject_bt, rowSubject, false);

       ProgressBar pb =  (ProgressBar) ((RelativeLayout)subjectLayout.getChildAt(0)).getChildAt(1);
       pb.setVisibility(View.GONE);

        // Ссылка на кнопку в шаблоне
        Button subjectBt = (Button) ((RelativeLayout)subjectLayout.getChildAt(0)).getChildAt(0);
        subjectBt.setText(R.string.full_statistic);

        // Устанавка изображения дисциплины, если оно есть
        BitmapDrawable img = new BitmapDrawable(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.ic_statistics));;
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        int sizeIm = (int) (size.x * 0.5 / 4.2);
        img.setBounds(0, sizeIm / 6, sizeIm, sizeIm + sizeIm / 6);
        subjectBt.setCompoundDrawables(null, img, null, null);

        // Устанавливаем функционал кнопке
        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                TransitionDrawable transitionDrawable = (TransitionDrawable) view.getBackground();
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    transitionDrawable.startTransition(150);
                    view.startAnimation(AnimationUtils.loadAnimation(SubjectListActivity.this, R.anim.btn_pressed));
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SubjectListActivity.this);
                    startActivity( new Intent(SubjectListActivity.this, SubjectInfoFullStatisticActivity.class), options.toBundle());

                    transitionDrawable.reverseTransition(150);
                    view.startAnimation(AnimationUtils.loadAnimation(SubjectListActivity.this, R.anim.btn_unpressed));

                }
                return true;
            }
        };

        subjectBt.setOnTouchListener(onTouchListener); // Добавляем функционал кнопке

        // Скрытие строки прогресса (здесь в ней нет необходимости)
        (subjectLayout.getChildAt(1)).setVisibility(View.INVISIBLE);
      //  subjectLayout.setVisibility(View.INVISIBLE);

        rowSubject.addView(subjectLayout); // Добавляем дисциплину
    }

    //создаем список предметов
    public void setSubjectsList() {

        TableRow rowSubject = null;

        int i;
        subjectDrawables = new SubjectDrawable[Subjects.subjectsList.length];

        for (i = 0; i <= Subjects.subjectsList.length; ++i) {
            // В одном ряду может быть лишь 3 кнопки, если уже три созданы, создается следующая колонка
            if (i % 3 == 0) {

                ViewGroup.LayoutParams params = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
                rowSubject = new TableRow(this);
                rowSubject.setLayoutParams(params);
                rowSubject.setOrientation(TableRow.HORIZONTAL);
                rowSubject.setWeightSum(3f);
                tableSubjects.addView(rowSubject);
            }

            if (i == Subjects.subjectsList.length)
                createFullStatistics(rowSubject);
            else {
                subjectDrawables[i] = new SubjectDrawable();
                subjectDrawables[i].createSubject(rowSubject, i);
            }
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

        new LoadSubject().execute();
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
        for (int i = 0; i < subjectDrawables.length; ++i)
            subjectDrawables[i].textProgress.setText(progresses[i] + " %");
    }

    class LoadSubject extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            BitmapDrawable img = new BitmapDrawable(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.empty));
            for (int i = 0; i < subjectDrawables.length; ++i)
                subjectDrawables[i].setImg(img);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (int i = 0; i < subjectDrawables.length; ++i) {
                while (Subjects.getSubjectImage(getApplicationContext(), Subjects.subjectsList[i]) == null){};
                publishProgress(i);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... index) {
            BitmapDrawable img = Subjects.getSubjectImage(getApplicationContext(), Subjects.subjectsList[index[0]]);
            subjectDrawables[index[0]].setImg(img);
            subjectDrawables[index[0]].progressBar.setVisibility(View.GONE);
            return;
        }
    }
}
