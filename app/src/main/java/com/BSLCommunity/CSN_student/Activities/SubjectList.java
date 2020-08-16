package com.BSLCommunity.CSN_student.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_list);
        refBtn = (Button) findViewById(R.id.activity_subject_list_reference);
        refBtn.setVisibility(View.GONE); //выключаем референсную кнопку

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
        }
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
