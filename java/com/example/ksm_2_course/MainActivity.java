    package com.example.ksm_2_course;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    CountDownTimer start;
    final String FILE_NAME = "data_disc.json";
    Button res;
    ArrayList<Discipline> discs = new ArrayList<Discipline>(); //Дисциплины
    long seconds, hour, minutes;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        res = (Button) findViewById(R.id.res);
        setProgress();
        checkRegistration();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                Intent intent;
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void OnClickLessons(View v) {
        Intent intent;
        intent = new Intent(this, Lessons.class);
        startActivity(intent);
    }

    public void OnClick(View v) {
        Intent intent;
        intent = new Intent(this, Disciplines.class);
        intent.putExtra("Name", ((Button) v).getText());
        startActivity(intent);
        setProgress();
    }

    @Override
    protected void onResume() {
        setProgress();
        checkTimer();
        super.onResume();
    }

    @Override
    protected void onPause() {
        start.cancel();
        super.onPause();
    }
    
    public void setProgress() {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Discipline>>() {
        }.getType();
        discs = gson.fromJson(JSONHelper.read(this, FILE_NAME), listType);

        int sum = 0, all = 0;
        for (int i = 0; i < discs.size(); ++i) {
            Discipline temp = discs.get(i);
            sum += temp.getProgress();
            all += temp.getLabs();
        }
        all *= 2;

        ((Button) findViewById(R.id.res)).setText(Integer.toString(sum * 100 / all) + "%");
    }

    public void time() {
        //нужные переменные
        Calendar calendar = Calendar.getInstance();
        int currentTimeH = calendar.get(Calendar.HOUR_OF_DAY), currentTimeM = calendar.get(Calendar.MINUTE), currentTimeS = calendar.get(Calendar.SECOND);
        int currentTime = currentTimeH * 60 * 60 + currentTimeM * 60 + currentTimeS, endTime = 0;
        TextView timeUntil = (TextView) findViewById(R.id.timeUntil);
        TextView Time = (TextView) findViewById(R.id.Time);

        //начало и конец пары (в секундах)
        int[][] lessons = {{510 * 60, 590 * 60}, {605 * 60, 685 * 60}, {715 * 60, 795 * 60}, {805 * 60, 885 * 60}, {895 * 60, 975 * 60}};
        String[] romeNum = {"I", "II", "III", "IV", "V"};

        //нахожу какая сейчас пара

        if (currentTime > lessons[0][0] && currentTime < lessons[4][0]) {
            for (int i = 1; i < 5; ++i) {
                if (currentTime < lessons[i][0]) {
                    if (currentTime < lessons[i - 1][1]) {
                        endTime = lessons[i - 1][1] - currentTime;
                        timeUntil.setText("До кінця пари:");
                    } else {
                        endTime = lessons[i][0] - currentTime;
                        timeUntil.setText("Початок " + romeNum[i] + " пари:");
                    }
                    break;
                }
            }
        } else {
            timeUntil.setText("Початок I пари:");
            if (currentTime > lessons[0][0])
                endTime = 24 * 60 * 60 - currentTime + lessons[0][0];
            else endTime = lessons[0][0] - currentTime;
        }
        timer(endTime * 1000);
    }

    public void timer(int millis) {
        final TextView Time = (TextView) findViewById(R.id.Time);
        int milli = millis / 1000;
        seconds = milli % 60;
        minutes = (milli / 60) % 60;
        hour = milli / 3600;
        start = new CountDownTimer(millis, 1000) {
            String twoComm1 = ":", twoComm2 =":", shour = "", smin = "", ssec = "" ;


            @Override
            public void onTick(long millisUntilFinished) {

                --seconds;
                if (seconds < 0) {
                    seconds = 59;
                    --minutes;
                    if (minutes < 0) {
                        minutes = 59;
                        --hour;
                    }
                }


                //проверка на добавление 0 в минутах
                if (minutes < 10) {
                    smin =("0" + Long.toString(minutes));
                } else {
                    smin = (Long.toString(minutes));
                }
                //проверка на удаление часов при минутах
                if (hour != 0) {
                    shour = (Long.toString(hour));
                } else {
                    shour = ("");
                    twoComm1 = ("");
                }
                if (seconds < 10 &&  minutes != 0) {
                    ssec = ("0" + Long.toString(seconds));
                } else {
                    ssec = (Long.toString(seconds));
                }
                if( minutes == 0 && hour == 0){
                    twoComm2 = ("");
                    twoComm1 = ("");
                    smin = "";

                }
                Time.setText(shour + twoComm1 + smin + twoComm2 + ssec);
            }

            @Override
            public void onFinish() {
                time();
            }
        }.start();
    }

    public void times(){
        Date currentDate = new Date();
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String timeText = timeFormat.format(currentDate);
        TextView Time = (TextView) findViewById(R.id.Time);
        Time.setText(timeText);
    }

    public void checkTimer() {
        final TextView timeUntil = (TextView) findViewById(R.id.timeUntil);

        TextView Time = (TextView) findViewById(R.id.Time);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        boolean timer_settings = sharedPreferences.getBoolean(SettingsActivity.KEY_TIMER_SETTING, true);
        if (!timer_settings) {
            String twoComm1 = ":", twoComm2 =":", shour = "", smin = "", ssec = "" ;
            Time.setText(shour + twoComm1 + smin + twoComm2 + ssec);
        } else time();
    }

    public void checkRegistration() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean is_registered = sharedPreferences.getBoolean(SettingsActivity.KEY_IS_REGISTERED, true);

        if (is_registered) {
            Intent intent;
            intent = new Intent(this, Registration.class);
            startActivity(intent);
        } else {
            return;
        }
    }

}

