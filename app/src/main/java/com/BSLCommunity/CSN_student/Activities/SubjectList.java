package com.BSLCommunity.CSN_student.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.BSLCommunity.CSN_student.Objects.Subjects;
import com.BSLCommunity.CSN_student.R;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Locale;
import java.util.concurrent.Callable;
import static com.BSLCommunity.CSN_student.Objects.Subjects.getSubjectsList;

public class SubjectList extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_list);

        //получаем список групп
        getSubjectsList(this, new Callable<Void>() {
            @Override
            public Void call(){
                setSubjectsList();
                return null;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    public void setSubjectsList(){
        //достаем параметры референсной кнопки  затем ее выключаем
        Button refBtn = (Button) findViewById(R.id.activity_subject_list_reference);
        ViewGroup.LayoutParams refParams = refBtn.getLayoutParams();
        refBtn.setVisibility(View.GONE);

        //устанавливаем кнопки  предметов
        String subjectName = "";
        for(int i=0; i< Subjects.subjectsList.length;++i){
            try{
                //получаем имя предмета по локализации
                JSONObject subjectJSONObject = new JSONObject(Subjects.subjectsList[i].NameDiscipline);
                subjectName = subjectJSONObject.getString(Locale.getDefault().getLanguage());
            }catch (JSONException e){
            }

            //обработчик нажатию на кнопку
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Animation click = AnimationUtils.loadAnimation(SubjectList.this, R.anim.btn_click);
                    view.startAnimation(click);
                    Intent intent = new Intent(SubjectList.this, SubjectInfo.class);
                    intent.putExtra("button_id", view.getId());
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
    }





    /*public void setProgress() {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Subjects>>() {
        }.getType();
        discs = gson.fromJson(JSONHelper.read(this, Main.FILE_NAME), listType);

        int sum = 0, all = 0;
        for (int i = 0; i < discs.size(); ++i) {
            Subjects temp = discs.get(i);

            boolean[][] temp_bool = temp.getComplete();
            for (int j = 0; j < temp_bool.length; ++j) {
                sum += temp_bool[j][0] ? 1 : 0;
                sum += temp_bool[j][1] ? 1 : 0;
            }
            all += temp.getLabs();
        }
        all *= 2;

        ((Button) findViewById(R.id.res)).setText(Integer.toString(sum * 100 / all) + "%");
    }*/
}
