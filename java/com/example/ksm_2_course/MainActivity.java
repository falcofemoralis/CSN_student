package com.example.ksm_2_course;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    String FILE_NAME = "data_disc_";

    boolean whole = true;
    CountDownTimer start;
    RequestQueue requestQueue;
    Button res;
    ArrayList<Discipline> discs = new ArrayList<Discipline>(); //Дисциплины
    long seconds, hour, minutes;
    SharedPreferences pref;
    public static String MAIN_URL = "http://192.168.1.3/Rating/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        res = (Button) findViewById(R.id.res);
        pref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

        FILE_NAME += pref.getString(SettingsActivity.KEY_NICKNAME, "") + ".json";
        checkRegistration();

        try {
            if(checkConnection()) {
                showDialog();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void OnClickSettings(View v) {
        Animation click = AnimationUtils.loadAnimation(this, R.anim.btn_click);
        v.startAnimation(click);
        Intent intent;
        intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.bottom_in,R.anim.top_out);
    }

    public void OnClickLessons(View v) {
        Animation click = AnimationUtils.loadAnimation(this, R.anim.btn_click);
        v.startAnimation(click);
        Intent intent;
        intent = new Intent(this, Lessons.class);
        startActivity(intent);
    }

    public void OnClickRating(View v) {
        ArrayList<Discipline> discs = new ArrayList<Discipline>();
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Discipline>>() {}.getType();
        discs = gson.fromJson(JSONHelper.read(this, FILE_NAME), listType);

        for (int i = 0; i < discs.size(); ++i)
        {
            Discipline temp = discs.get(i);
            updateRating(pref.getString(SettingsActivity.KEY_NICKNAME, ""), temp.getName(), gson.toJson(temp.getComplete()));
        }
        Animation click = AnimationUtils.loadAnimation(this, R.anim.btn_click);
        v.startAnimation(click);
        Intent intent;
        intent = new Intent(this, Rating.class);
        startActivity(intent);
    }

    public void OnClick(View v) {
        Animation click = AnimationUtils.loadAnimation(this, R.anim.btn_click);
        v.startAnimation(click);
        Intent intent;
        intent = new Intent(this, Disciplines.class);
        intent.putExtra("Name", ((Button) v).getText());
        startActivity(intent);
        setProgress();
        overridePendingTransition(R.anim.bottom_in,R.anim.top_out);
    }

    @Override
    protected void onResume() {
        setProgress();
        checkTimer();
        super.onResume();
    }

    @Override
    protected void onPause() {
        boolean timer_settings = pref.getBoolean(SettingsActivity.KEY_TIMER_SETTING, true);
        if (timer_settings)  start.cancel();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
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

    public void checkTimer() {
        final TextView timeUntil = (TextView) findViewById(R.id.timeUntil);

        TextView Time = (TextView) findViewById(R.id.Time);
        TextView TimeUntil = (TextView) findViewById(R.id.timeUntil);

        boolean timer_settings = pref.getBoolean(SettingsActivity.KEY_TIMER_SETTING, true);
        if (!timer_settings) {
            Time.setText("");
            TimeUntil.setText("");
        } else time();
    }

    public void checkRegistration() {
        Boolean is_registered = pref.getBoolean(SettingsActivity.KEY_IS_REGISTERED, true);

        if (is_registered) {
            Intent intent;
            intent = new Intent(this, Login.class);
            startActivity(intent);
        } else {
            return;
        }
    }

    protected void updateRating( final String NickName, final String NameDiscp, final String status)
    {
        String url = MainActivity.MAIN_URL + "updateRating.php";

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            { }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(MainActivity.this, "No connection with our server,try later...", Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("NickName", NickName);
                parameters.put("NameDiscp", NameDiscp);
                parameters.put("Status", status);
                return parameters;
            }
        };
        requestQueue.add(request);
    }

    protected void showDialog()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

        String PATH = this.getFileStreamPath(FILE_NAME).toString();
        File file = new File(PATH);
        if (file.exists())
        {
            long last = file.lastModified();
            Date date = new Date(last);
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy, HH:mm:ss");
            alertDialog.setTitle("Найдены локальные данные за " + format.format(date));
        }
        else{
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Discipline>>() {}.getType();
            discs = gson.fromJson(JSONHelper.read(MainActivity.this, "data_disc.json"), listType);

            for (int i = 0; i < discs.size(); ++i)
                getStatus(pref.getString(SettingsActivity.KEY_NICKNAME, ""), discs.get(i), i);
            return;
        }


        alertDialog.setMessage("Взять данные с сервера или с устройства?");

        alertDialog.setPositiveButton("Устройство", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<Discipline>>() {}.getType();
                discs = gson.fromJson(JSONHelper.read(MainActivity.this, FILE_NAME), listType);

                for (int i = 0; i < discs.size(); ++i)
                {
                    Discipline temp = discs.get(i);
                    updateRating(pref.getString(SettingsActivity.KEY_NICKNAME, ""), temp.getName(), gson.toJson(temp.getComplete()));
                }
            }
        });

        alertDialog.setNegativeButton("Сервер", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<Discipline>>() {}.getType();
                discs = gson.fromJson(JSONHelper.read(MainActivity.this, FILE_NAME), listType);

                for (int i = 0; i < discs.size(); ++i)
                    getStatus(pref.getString(SettingsActivity.KEY_NICKNAME, ""), discs.get(i), i);
            }
        });

        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    protected void saveJSON()
    {
        Gson gson = new Gson();
        String jsonString = gson.toJson(discs);
        JSONHelper.create(MainActivity.this, FILE_NAME, jsonString);
        setProgress();
        whole = true;
    }

    class result { public String status;}
    protected void getStatus (final String NickName, final Discipline current, final int num)
    {

        String url = MainActivity.MAIN_URL + "getStatus.php";

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                if(!response.equals("null")){
                    Gson gson = new Gson();
                    boolean[][] compl_but = gson.fromJson(gson.fromJson(response, result.class).status, boolean[][].class);
                    current.setComplete(compl_but);

                    if (num == discs.size() - 1)
                        saveJSON();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (whole) {
                    Toast.makeText(MainActivity.this, "No connection with our server,try later...", Toast.LENGTH_LONG).show();
                    whole = false;
                }
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("NickName", NickName);
                parameters.put("NameDiscp", current.getName());
                return parameters;
            }
        };
        requestQueue.add(request);
    }

    public boolean checkConnection () throws InterruptedException {
        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
}



