        package com.example.ksm_2_course;

    import androidx.appcompat.app.AppCompatActivity;
    import android.content.Intent;
    import android.os.Bundle;
    import android.os.CountDownTimer;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.Button;
    import android.widget.RadioButton;
    import android.widget.TextView;
    import com.google.gson.Gson;
    import com.google.gson.reflect.TypeToken;
    import java.lang.reflect.Type;
    import java.util.ArrayList;
    import java.util.Calendar;
    import java.util.List;


    public class MainActivity extends AppCompatActivity {

        final String FILE_NAME = "data_disc.json",FILE="defaultRadioButton.json";
        public static int StatusButton;
        Button res;
        ArrayList<Discipline> discs = new ArrayList<Discipline>(); //Дисциплины

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            res = (Button) findViewById(R.id.res);
            setProgress();
            restore();
            time();
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
                        if (currentTime < lessons[i - 1][1])
                            endTime = lessons[i - 1][1] - currentTime;
                        else {
                            endTime = lessons[i][0] - currentTime;
                            timeUntil.setText("Початок " + romeNum[i] + " пари:");
                        }
                        break;
                    }
                }
            }
            else {
                timeUntil.setText("Початок I пари:");
                if (currentTime > lessons[0][0]) endTime = 24 * 60 * 60 - currentTime + lessons[0][0];
                else endTime = lessons[0][0] - currentTime;
            }
                timer(endTime * 1000);
        }

            public void timer ( int millis)
            {
                final TextView timerS = (TextView) findViewById(R.id.timerS);
                final TextView timerM = (TextView) findViewById(R.id.timerM);
                final TextView timerH = (TextView) findViewById(R.id.timerH);
                final TextView twoCommas2 = (TextView) findViewById(R.id.twoCommas2);

                CountDownTimer start = new CountDownTimer(millis, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

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

    }

