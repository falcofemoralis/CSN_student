package com.example.ksm_2_course;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    final String FILE_NAME = "data_disc.json";
    public static int StatusButton=1;
    Button res;
    int RES;
    ArrayList<Discipline> discs = new ArrayList<Discipline>(); //Дисциплины
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        res = (Button) findViewById(R.id.res);
        setProgress();
    }

   public void setProgress()
    {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Discipline>>() {}.getType();
        discs = gson.fromJson(JSONHelper.read(this, FILE_NAME), listType);

        int sum = 0, all = 0;
        for (int i = 0; i < discs.size(); ++i)
        {
            Discipline temp = discs.get(i);
            sum += temp.getProgress();
            all += temp.getLabs();
        }
        all *= 2;

        ((Button)findViewById(R.id.res)).setText(Integer.toString(sum * 100 / all) + "%");
    }


    public void  OnClickRadioButton(View v){
         RadioButton radioButton_knt528 = (RadioButton) findViewById(R.id.radioButton_knt528);

         if(radioButton_knt528.isChecked()){
             radioButton_knt528.setChecked(false);
         }
        StatusButton=0;

    }

    public void  OnClickRadioButton2(View v){
        RadioButton radioButton_knt518 = (RadioButton) findViewById(R.id.radioButton_knt518);
        if(radioButton_knt518.isChecked()){
            radioButton_knt518.setChecked(false);
        }
        StatusButton=1;

    }
    public  void OnClickLessons_schedule(View v){
        Intent intent;
        intent = new Intent(this, Lessons_schedule.class);
        startActivity(intent);
    }
   public void OnClick(View v)
    {
        Intent intent;
        intent = new Intent(this, Disciplines.class);
        intent.putExtra("Name", ((Button) v).getText());
        startActivity(intent);

        setProgress();
    }
}

