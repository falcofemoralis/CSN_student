package com.example.ksm_2_course;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    final String FILE_NAME = "data_disc.json",FILE="defaultRadioButton.json";
    public static int StatusButton;
    Button res;
    ArrayList<Discipline> discs = new ArrayList<Discipline>(); //Дисциплины
    static int endTimeM,endTimeH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        res = (Button) findViewById(R.id.res);
        setProgress();
        restore();

        //////////////////////////////////////////////
                    //Таймер//
        //////////////////////////////////////////////

        Calendar calendar = Calendar.getInstance();

        int currentTimeH = calendar.get(Calendar.HOUR_OF_DAY),currentTimeM = calendar.get(Calendar.MINUTE),currentTimeS = calendar.get(Calendar.SECOND);
        int currentTime = currentTimeH*60+currentTimeM;
        TextView timeUntil = (TextView) findViewById(R.id.timeUntil);

        //начало и конец пары (в минутах)
        int[][] lessons = { { 510, 590 }, { 605, 685 }, { 715, 795}, { 805, 885}, { 895, 975 } };

        //нахожу какая сейчас пара
        //endTimeM времени осталось до конца пары (в минутах)
        if(currentTime>=lessons[0][0] && currentTime<lessons[0][1]){
            endTimeM = lessons[0][1]-currentTime;
            time(currentTimeS);

        }else if(currentTime>=lessons[1][0] && currentTime<lessons[1][1]){
            endTimeM = lessons[1][1]-currentTime;
            time(currentTimeS);

        }else if(currentTime>=lessons[2][0] && currentTime<lessons[2][1]){
            endTimeM = lessons[2][1]-currentTime;
            time(currentTimeS);

        }else if(currentTime>=lessons[3][0] && currentTime<lessons[3][1]){
            endTimeM = lessons[3][1]-currentTime;
            time(currentTimeS);

        }else if(currentTime>=lessons[4][0] && currentTime<lessons[4][1]){
            endTimeM = lessons[4][1]-currentTime;
            time(currentTimeS);

        }else if(currentTime>=lessons[0][1] && currentTime<lessons[1][0]){
            timeUntil.setText("Початок II пари:");
            endTimeM = lessons[1][0]-currentTime;
            time(currentTimeS);

        }else if(currentTime>=lessons[1][1] && currentTime<lessons[2][0]){
            timeUntil.setText("Початок III пари:");
            endTimeM = lessons[2][0]-currentTime;
            time(currentTimeS);

        }else if(currentTime>=lessons[2][1] && currentTime<lessons[3][0]){
            timeUntil.setText("Початок IV пари:");
            endTimeM = lessons[3][0]-currentTime;
            time(currentTimeS);

        }else if(currentTime>=lessons[3][1] && currentTime<lessons[4][0]){
            timeUntil.setText("Початок V пари:");
            endTimeM = lessons[4][0]-currentTime;
            time(currentTimeS);

        }else{
            timeUntil.setText("Початок I пари:");
            if(currentTime>lessons[0][0]) endTimeM = 24*60-currentTime+lessons[0][0];
            else endTimeM = lessons[0][0]-currentTime;
            time(currentTimeS);
        }
    }

    public void time(int currentTime){
        TextView textView12 = (TextView) findViewById(R.id.textView12);
        endTimeH=endTimeM/60;
        endTimeM%=60;
        --endTimeM;
        if(endTimeH==0) textView12.setText(" ");
        timer((60-currentTime)*1000);
    }
    public void timer(int seconds){
        final TextView timerS = (TextView) findViewById(R.id.timerS);
        TextView timerM = (TextView) findViewById(R.id.timerM);
        TextView timerH = (TextView) findViewById(R.id.timerH);
        String buff = Integer.toString(endTimeM);
        if(endTimeM<10){
            timerM.setText("0"+buff);

        }else{
            timerM.setText(Integer.toString(endTimeM));
        }


        if(endTimeH!=0) timerH.setText(Integer.toString(endTimeH));
        new CountDownTimer(seconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if(millisUntilFinished<10000){
                    String buff = Long.toString(millisUntilFinished/1000);
                    timerS.setText("0"+buff);
                }else{
                    timerS.setText("" + millisUntilFinished/1000);
                }

            }

            @Override
            public void onFinish() {
                --endTimeM;
                if(endTimeM>0) timer(60000);
                else if(endTimeH>0){
                    --endTimeH;
                    endTimeM=60;
                }
            }
        }.start();
    }

    @Override
    protected void onResume()
    {
        setProgress();
        super.onResume();
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
         save();
    }

    public void  OnClickRadioButton2(View v){
        RadioButton radioButton_knt518 = (RadioButton) findViewById(R.id.radioButton_knt518);
        if(radioButton_knt518.isChecked()){
            radioButton_knt518.setChecked(false);
        }
        StatusButton=1;
        save();
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

    public void save(){
        JSONHelper.create(this, FILE , Integer.toString(StatusButton));
    }

    public void restore(){
        RadioButton radioButton_knt518 = (RadioButton) findViewById(R.id.radioButton_knt518);
        RadioButton radioButton_knt528 = (RadioButton) findViewById(R.id.radioButton_knt528);
        Gson gson = new Gson();
            String StatusButtonString = gson.fromJson(JSONHelper.read(this,FILE),String.class);
            StatusButton = Integer.parseInt(StatusButtonString);
            if(StatusButton==0){
                radioButton_knt518.setChecked(true);
            }else if(StatusButton ==1){
                radioButton_knt528.setChecked(true);
            }


    }


}

