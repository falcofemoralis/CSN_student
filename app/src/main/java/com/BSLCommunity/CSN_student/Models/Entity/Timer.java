package com.BSLCommunity.CSN_student.Models.Entity;

import android.os.CountDownTimer;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class Timer extends AppCompatActivity {
    public enum TimeType {
        UNTIL_START,
        UNTIL_END
    }

    public interface ITimer {
        void updateTime(String time);

        void updatePair(int pair, TimeType type);
    }

    private long seconds, hour, minutes;
    private CountDownTimer countDownTimer;
    public static final String[][] times = {{"8:30", "9:50"}, {"10:05", "11:25"}, {"11:55", "13:15"}, {"13:25", "14:45"}, {"14:55", "16:15"}};
    public static final String[] romeNum = {"I", "II", "III", "IV", "V"};
    public static final String[] breaks = {"15", "30", "10", "10", "30"};
    public ITimer iTimer;

    public Timer(ITimer iTimer) {
        this.iTimer = iTimer;
        calculateTime();
    }

    /**
     * Получение пары в римском представлении
     *
     * @param i - индекс пары
     * @return - номер пары в римском стиле
     */
    public static String getRomePair(int i) {
        return romeNum[i];
    }

    /**
     * Расчет времени отсчета
     */
    private void calculateTime() {
        //нужные переменные
        Calendar calendar = Calendar.getInstance();
        int currentTimeH = calendar.get(Calendar.HOUR_OF_DAY), currentTimeM = calendar.get(Calendar.MINUTE), currentTimeS = calendar.get(Calendar.SECOND);
        int currentTime = currentTimeH * 60 * 60 + currentTimeM * 60 + currentTimeS, endTime = 0;

        //начало и конец пары (в секундах)
        int[][] lessons = new int[times.length][2];


        for (int i = 0; i < times.length; i++) {
            for (int j = 0; j < 2; j++) {
                String tmp = "";
                for (int k = 0; k < times[i][j].length(); k++) {
                    if (times[i][j].charAt(k) != ':') {
                        tmp += times[i][j].charAt(k);
                    } else {
                        lessons[i][j] = Integer.parseInt(tmp) * 60 * 60;
                        tmp = "";
                    }
                    if (k == times[i][j].length() - 1)
                        lessons[i][j] += Integer.parseInt(tmp) * 60;
                }
            }
        }

        //нахожу какая сейчас пара
        if (currentTime > lessons[0][0] && currentTime < lessons[lessons.length - 1][1]) {
            for (int i = 0; i < lessons.length; ++i) {
                if (currentTime > lessons[i][0] && currentTime < lessons[i][1]) {
                    // Пара
                    endTime = lessons[i][1] - currentTime;
                    iTimer.updatePair(i, TimeType.UNTIL_END);
                    break;
                } else if (currentTime > lessons[i][1] && currentTime < lessons[i + 1][0]) {
                    // Перемена
                    endTime = lessons[i + 1][0] - currentTime;
                    iTimer.updatePair(i + 1, TimeType.UNTIL_START);
                    break;
                }
            }
        } else {
            // Время между последней и первой парами
            iTimer.updatePair(0, TimeType.UNTIL_START);

            if (currentTime > lessons[0][0])
                endTime = 24 * 60 * 60 - currentTime + lessons[0][0];
            else
                endTime = lessons[0][0] - currentTime;
        }

        startTimer(endTime * 1000);
    }

    /**
     * Запуск времени отсчета
     *
     * @param millis - кол-во миллисекунд отсчета
     */
    private void startTimer(int millis) {
        int milli = millis / 1000;
        seconds = milli % 60;
        minutes = (milli / 60) % 60;
        hour = milli / 3600;

        countDownTimer = new CountDownTimer(millis, 1000) {
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
                iTimer.updateTime(shour + twoComm1 + smin + twoComm2 + ssec);
            }

            @Override
            public void onFinish() {
                calculateTime();
            }
        }.start();
    }

    public void stop() {
        countDownTimer.cancel();
    }
}
