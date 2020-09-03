package com.BSLCommunity.CSN_student.Activities;

import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.BSLCommunity.CSN_student.Managers.AnimationManager;
import com.BSLCommunity.CSN_student.Objects.Subjects;
import com.BSLCommunity.CSN_student.Objects.SubjectsInfo;
import com.BSLCommunity.CSN_student.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class SubjectInfoFullStatisticActivity extends AppCompatActivity {
    TableLayout worksTL;
    TableRow worksNumberTR;
    ArrayList<Integer> maxWorks = new ArrayList<>();
    final int TYPES_WORKS_COUNT = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnimationManager.setAnimation(getWindow(), this);
        setContentView(R.layout.activity_subject_info_full_statistic);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        worksTL = findViewById(R.id.activity_subject_info_full_tl_works);
        worksNumberTR = findViewById(R.id.activity_subject_info_full_tr_works_numbers);

        for (int i=0;i<TYPES_WORKS_COUNT;++i) maxWorks.add(0); //инициализируем
        for(int i=0;i< Subjects.subjectsList.length;++i) addSubject(i); //добавлем полосу предмета
        addWorksHeaders(); //добавляем заголовки
    }

    public void addSubject(int id) {
        //получаем данные предмета
        SubjectsInfo.SubjectInfo subject = SubjectsInfo.getInstance(this).subjectInfo[id];

        //узнаем кол-во работ
        ArrayList<Integer> worksCount = new ArrayList<>(); //кол-во работ у текущего предмета
        for (int i=0;i<TYPES_WORKS_COUNT;++i){
            int workCount = mGetCount(i,subject);
            worksCount.add(workCount);
            if(workCount>maxWorks.get(i)) maxWorks.add(i,workCount);       //запоминаем макс кол-вол работ
        }

        //добавляем строку данных предмета
        TableRow tableRow = new TableRow(this);

        //добавляем ценность предмета
        TextView value = mGetView(R.layout.inflate_statistic_view);
        ArrayAdapter values = ArrayAdapter.createFromResource(
                this,
                R.array.values,
                R.layout.spinner_subjectinfo_layout
        );
        value.setText(values.getItem(subject.subjectValue).toString());
        value.setTextColor(getColor(R.color.white));
        tableRow.addView(value);

        //добавляем название предмета
        TextView subjectName = mGetView(R.layout.inflate_statistic_view);
        try {
            JSONObject subjectJSONObject = new JSONObject(Subjects.subjectsList[id].NameDiscipline);
            subjectName.setText(subjectJSONObject.getString(Locale.getDefault().getLanguage()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        subjectName.setTextColor(getColor(R.color.white));
        subjectName.setGravity(Gravity.LEFT);
        tableRow.addView(subjectName);

        //тип работы у предмета
        ArrayAdapter types = ArrayAdapter.createFromResource(
                this,
                R.array.work_types,
                R.layout.spinner_subjectinfo_layout
        );

        //добавляем работы
        for (int j=0;j<TYPES_WORKS_COUNT;++j) {
            for (int i = 0; i < worksCount.get(j); ++i) {
                TextView work = mGetView(R.layout.inflate_statistic_view);
                int valueId = mGetValue(j,subject, i);
                work.setText(types.getItem(valueId).toString());
                work.setTextColor(getColor(R.color.white));
                Drawable drawable = getDrawable(R.drawable.inflate_drawable_statistic_view);
                drawable.mutate();
                drawable.setTint(getColor(SubjectsInfo.colors[valueId]));
                work.setBackgroundDrawable(drawable);
                tableRow.addView(work);
            }
        }

        worksTL.addView(tableRow);
    }

    public void addWorksHeaders() {
        TextView valueHeader =  mGetView(R.layout.inflate_statistic_view_header);
        valueHeader.setText(R.string.value);
        worksNumberTR.addView(valueHeader);

        TextView subjectHeader = mGetView(R.layout.inflate_statistic_view_header);
        subjectHeader.setText(R.string.subject);
        worksNumberTR.addView(subjectHeader);

        for (int i = 0; i < maxWorks.get(0); ++i) {
            TextView textView =mGetView(R.layout.inflate_statistic_view_header);
            textView.setText("lab " + (i + 1));
            textView.setTextColor(getColor(R.color.white));
            worksNumberTR.addView(textView);
        }
        for (int i = 0; i < maxWorks.get(1); ++i) {
            TextView textView = mGetView(R.layout.inflate_statistic_view_header);
            textView.setText("ihw " + (i + 1));
            textView.setTextColor(getColor(R.color.white));
            worksNumberTR.addView(textView);
        }
        for (int i = 0; i < maxWorks.get(2); ++i) {
            TextView textView = mGetView(R.layout.inflate_statistic_view_header);
            textView.setText("other " + (i + 1));
            textView.setTextColor(getColor(R.color.white));
            worksNumberTR.addView(textView);
        }
    }

    private int mGetCount(int type, SubjectsInfo.SubjectInfo work){
        switch (type){
            case 0: return work.labs.count;
            case 1: return work.ihw.count;
            case 2: return work.others.count;
            default: return 0;
        }
    }

    private int mGetValue(int type, SubjectsInfo.SubjectInfo work, int id){
        switch (type){
            case 0: return work.labs.values.get(id);
            case 1: return work.ihw.values.get(id);
            case 2: return work.others.values.get(id);
            default: return 0;
        }
    }

    private TextView mGetView(int id){
        TextView textView = (TextView) LayoutInflater.from(this).inflate(id, null);
        return textView;
    }
}
