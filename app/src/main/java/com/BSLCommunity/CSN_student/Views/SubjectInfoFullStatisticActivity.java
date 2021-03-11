package com.BSLCommunity.CSN_student.Views;

import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.BSLCommunity.CSN_student.Managers.AnimationManager;
import com.BSLCommunity.CSN_student.Models.SubjectsInfo;
import com.BSLCommunity.CSN_student.R;

public class SubjectInfoFullStatisticActivity extends BaseActivity {
    TableLayout worksTL;
    TableRow worksNumberTR;
    final int TYPES_WORKS_COUNT = 3;
    int[] maxWorks = new int[TYPES_WORKS_COUNT];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnimationManager.setAnimation(getWindow(), this);
        setContentView(R.layout.activity_subject_info_full_statistic);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        worksTL = findViewById(R.id.activity_subject_info_full_tl_works);
        worksNumberTR = findViewById(R.id.activity_subject_info_full_tr_works_numbers);
//
//        for (int i = 0; i < SubjectModel.subjects.length; ++i) getMaxWorks(i); //узнаем максимальное кол-во работ (по типам)
//        for (int i = 0; i < SubjectModel.subjects.length; ++i) addSubjectRow(i); //добавлем полосу предмета
        addWorksHeaders(); //добавляем заголовки
    }

    //узнаем максимальное кол-во работ
    public  void getMaxWorks(int id){
        SubjectsInfo.SubjectInfo subject = SubjectsInfo.getInstance(this).subjectInfo[id];

        for (int j = 0; j < TYPES_WORKS_COUNT; ++j) {
            int workCount = mGetCount(j, subject);
            if (workCount > maxWorks[j])
                maxWorks[j] = workCount; //запоминаем макс кол-вол работ
        }
    }

    //добавлем полосу предмета
    public void addSubjectRow(int id) {
        //получаем данные предмета
        SubjectsInfo.SubjectInfo subject = SubjectsInfo.getInstance(this).subjectInfo[id];

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
//        try {
//            JSONObject subjectJSONObject = new JSONObject(SubjectModel.subjects[id].NameDiscipline);
//            subjectName.setText(subjectJSONObject.getString(LocaleHelper.getLanguage(this)));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
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
            for (int i = 0; i < maxWorks[j]; ++i) {
                //создаем поле работы
                TextView work = mGetView(R.layout.inflate_statistic_view);

                //получаем id ценности работы
                int valueId = mGetValue(j,subject, i);

                //если ценность у данной работы равно -1, значит ее не существует
                if(valueId != -1){
                    work.setText(types.getItem(valueId).toString());
                    work.setTextColor(getColor(R.color.white));
                    Drawable drawable = getDrawable(R.drawable.inflate_drawable_statistic_view);
                    drawable.mutate();
                    drawable.setTint(getColor(SubjectsInfo.colors[valueId]));
                    work.setBackgroundDrawable(drawable);
                }

                tableRow.addView(work);
            }
        }

        worksTL.addView(tableRow);
    }

    public void addWorksHeaders() {
        //заголовок ценности работ
        TextView valueHeader =  mGetView(R.layout.inflate_statistic_view_header);
        valueHeader.setText(R.string.value);
        worksNumberTR.addView(valueHeader);

        //заголовок предметов
        TextView subjectHeader = mGetView(R.layout.inflate_statistic_view_header);
        subjectHeader.setText(R.string.subject);
        worksNumberTR.addView(subjectHeader);

        //заголовки работ
        int [] workHeader = {R.string.lab, R.string.ihw, R.string.other};
        for (int i=0;i<TYPES_WORKS_COUNT;++i) {
            for (int j = 0; j < maxWorks[i]; ++j) {
                TextView textView =mGetView(R.layout.inflate_statistic_view_header);
                textView.setText(getString(workHeader[i]) + " " + (j + 1));
                textView.setTextColor(getColor(R.color.white));
                worksNumberTR.addView(textView);
            }
        }
    }

    private int mGetCount(int type, SubjectsInfo.SubjectInfo work){
        try {
            switch (type){
                case 0: return work.labs.count;
                case 1: return work.ihw.count;
                case 2: return work.others.count;
                default: return 0;
            }
        }catch (Exception e){
            return -1;
        }

    }

    private int mGetValue(int type, SubjectsInfo.SubjectInfo work, int id){
        try {
            switch (type){
                case 0: return work.labs.values.get(id);
                case 1: return work.ihw.values.get(id);
                case 2: return work.others.values.get(id);
                default: return 0;
            }
        }catch (Exception e){
            return -1;
        }

    }

    private TextView mGetView(int id){
        TextView textView = (TextView) LayoutInflater.from(this).inflate(id, null);
        return textView;
    }
}
