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


public class MainActivity extends AppCompatActivity {
    CountDownTimer start;
    final String FILE_NAME = "data_disc.json";
    Button res;
    ArrayList<Discipline> discs = new ArrayList<Discipline>(); //Дисциплины

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
        final TextView timerS = (TextView) findViewById(R.id.timerS);
        final TextView timerM = (TextView) findViewById(R.id.timerM);
        final TextView timerH = (TextView) findViewById(R.id.timerH);
        final TextView twoCommas2 = (TextView) findViewById(R.id.twoCommas2);
        final TextView twoCommas1 = (TextView) findViewById(R.id.twoCommas1);
        start = new CountDownTimer(millis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                twoCommas1.setText(":");
                //проверка на добавление нуля в секундах
                long seconds = (millisUntilFinished / 1000) % 60;
                if (seconds < 10) {
                    String ssec = Long.toString(seconds);
                    timerS.setText("0" + ssec);
                } else {
                    timerS.setText(Long.toString(seconds));
                }

                //проверка на добавление 0 в минутах
                long minutes = (millisUntilFinished / (1000 * 60)) % 60;
                if (minutes < 10) {
                    String smin = Long.toString(minutes);
                    timerM.setText("0" + smin);
                } else {
                    timerM.setText(Long.toString(minutes));
                }
                //проверка на удаление часов при минутах
                long hour = (millisUntilFinished / (1000 * 60)) / 60;
                if (hour != 0) {
                    timerH.setText(Long.toString(hour));
                    twoCommas2.setText(":");
                } else {
                    timerH.setText(" ");
                    twoCommas2.setText(" ");
                }
            }

            @Override
            public void onFinish() {
                time();
            }
        }.start();
    }

    public void checkTimer() {
        final TextView timerS = (TextView) findViewById(R.id.timerS);
        final TextView timerM = (TextView) findViewById(R.id.timerM);
        final TextView timerH = (TextView) findViewById(R.id.timerH);
        final TextView twoCommas2 = (TextView) findViewById(R.id.twoCommas2);
        final TextView twoCommas1 = (TextView) findViewById(R.id.twoCommas1);
        final TextView timeUntil = (TextView) findViewById(R.id.timeUntil);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        boolean timer_settings = sharedPreferences.getBoolean(SettingsActivity.KEY_TIMER_SETTING, true);
        if (!timer_settings) {
            start.cancel();
            timeUntil.setText("");
            twoCommas1.setText("");
            twoCommas2.setText("");
            timerS.setText("");
            timerM.setText("");
            timerH.setText("");
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

