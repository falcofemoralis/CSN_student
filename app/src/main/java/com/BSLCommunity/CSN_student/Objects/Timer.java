package com.BSLCommunity.CSN_student.Objects;


import android.content.res.Resources;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.BSLCommunity.CSN_student.Activities.SettingsActivity;
import com.BSLCommunity.CSN_student.R;

import java.util.Calendar;

import static com.BSLCommunity.CSN_student.Objects.Settings.encryptedSharedPreferences;

public class Timer extends AppCompatActivity {
    long seconds, hour, minutes;
    public CountDownTimer start;

    public void startTimer(int millis, final TextView TimeUntil, final TextView Time, final Resources res) {
        int milli = millis / 1000;
        seconds = milli % 60;
        minutes = (milli / 60) % 60;
        hour = milli / 3600;
        start = new CountDownTimer(millis, 1000) {
            String twoComm1 = ":", twoComm2 = ":", shour = "", smin = "", ssec = "";

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
                    smin = ("0" + Long.toString(minutes));
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
                if (seconds < 10 && minutes != 0) {
                    ssec = ("0" + Long.toString(seconds));
                } else {
                    ssec = (Long.toString(seconds));
                }
                if (minutes == 0 && hour == 0) {
                    twoComm2 = ("");
                    twoComm1 = ("");
                    smin = "";

                }
                Time.setText(shour + twoComm1 + smin + twoComm2 + ssec);
            }

            @Override
            public void onFinish() {
                time(TimeUntil, Time, res);
            }
        }.start();
    }

    public void checkTimer(TextView TimeUntil, TextView Time, Resources res) {
        boolean timer_settings = encryptedSharedPreferences.getBoolean(SettingsActivity.KEY_TIMER_SETTING, true);
        if (!timer_settings) {
            Time.setVisibility(View.GONE);
            TimeUntil.setVisibility(View.GONE);
        } else {
            Time.setVisibility(View.VISIBLE);
            TimeUntil.setVisibility(View.VISIBLE);
            time(TimeUntil, Time, res);
        }
    }

    public void time(TextView TimeUntil, TextView Time, Resources res) {
        //нужные переменные
        Calendar calendar = Calendar.getInstance();
        int currentTimeH = calendar.get(Calendar.HOUR_OF_DAY), currentTimeM = calendar.get(Calendar.MINUTE), currentTimeS = calendar.get(Calendar.SECOND);
        int currentTime = currentTimeH * 60 * 60 + currentTimeM * 60 + currentTimeS, endTime = 0;

        //начало и конец пары (в секундах)
        int[][] lessons = {{510 * 60, 590 * 60}, {605 * 60, 685 * 60}, {715 * 60, 795 * 60}, {805 * 60, 885 * 60}, {895 * 60, 975 * 60}};
        String[] romeNum = {"I", "II", "III", "IV", "V"};

        //нахожу какая сейчас пара
        if (currentTime > lessons[0][0] && currentTime < lessons[4][0]) {
            for (int i = 1; i < 5; ++i) {
                if (currentTime < lessons[i][0]) {
                    if (currentTime < lessons[i - 1][1]) {
                        endTime = lessons[i - 1][1] - currentTime;
                        TimeUntil.setText(res.getString(R.string.timeUntil));
                    } else {
                        endTime = lessons[i][0] - currentTime;
                        TimeUntil.setText(res.getString(R.string.start) + " " + romeNum[i] + " " + res.getString(R.string.lesson));
                    }
                    break;
                }
            }
        } else {
            TimeUntil.setText(R.string.first_lesson);
            if (currentTime > lessons[0][0])
                endTime = 24 * 60 * 60 - currentTime + lessons[0][0];
            else endTime = lessons[0][0] - currentTime;
        }
        startTimer(endTime * 1000, TimeUntil, Time, res);
    }

    public void resetTimer() {
        boolean timer_settings = encryptedSharedPreferences.getBoolean(SettingsActivity.KEY_TIMER_SETTING, true);
        if (timer_settings) start.cancel();
    }
}
