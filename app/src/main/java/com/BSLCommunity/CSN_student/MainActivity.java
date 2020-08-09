package com.BSLCommunity.CSN_student;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    //список групп по курсу
    class groups { public int Code_Group; public String GroupName;}
    public static groups[] GROUPS;

    public static String MAIN_URL = "http://fknt.web-file.site/";
    public static String NEW_MAIN_URL = "http://a0459938.xsph.ru/";

    public static String FILE_NAME = "data_disc_";
    boolean whole = true;
    CountDownTimer start;
    RequestQueue requestQueue;
    Button res;
    SharedPreferences.Editor prefEditor;
    ArrayList<Discipline> discs = new ArrayList<Discipline>(); //Дисциплины
    long seconds, hour, minutes;
    public static SharedPreferences encryptedSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setFile();
        res = (Button) findViewById(R.id.res);

        getGroups();
        checkRegistration();
        FILE_NAME += encryptedSharedPreferences.getString(Settings2.KEY_NICKNAME, "") + ".json";

        prefEditor = MainActivity.encryptedSharedPreferences.edit();

        boolean offline_data = encryptedSharedPreferences.getBoolean(Settings2.KEY_OFFLINE_DATA, false);

        if(checkConnection())
        {
            if (offline_data)
                showDialog();
            else
                loadStatusFromServer();
        }
        else
            loadStatusFromDevice();
    }

    public void OnClickSettings(View v) {
        Animation click = AnimationUtils.loadAnimation(this, R.anim.btn_click);
        v.startAnimation(click);
        startActivity(new Intent(this, Settings2.class));
    }

    public void OnClickLessons(View v) {
        Animation click = AnimationUtils.loadAnimation(this, R.anim.btn_click);
        v.startAnimation(click);
        startActivity(new Intent(this, Schedule.class));
    }

    public void OnClickRating(View v) {
        ArrayList<Discipline> discs = new ArrayList<Discipline>();
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Discipline>>() {}.getType();
        discs = gson.fromJson(JSONHelper.read(this, FILE_NAME), listType);

        for (int i = 0; i < discs.size(); ++i)
        {
            Discipline temp = discs.get(i);
            updateRating(encryptedSharedPreferences.getString(Settings2.KEY_NICKNAME, ""), temp.getName(), gson.toJson(temp.getComplete()), temp.getIDZ());
        }
        Animation click = AnimationUtils.loadAnimation(this, R.anim.btn_click);
        v.startAnimation(click);
        startActivity( new Intent(this, Rating.class));
    }

    public void OnClickSubjects(View v) {
        Animation click = AnimationUtils.loadAnimation(this, R.anim.btn_click);
        v.startAnimation(click);
        startActivity(new Intent(this, Subjects.class));
    }

    @Override
    protected void onResume() {
        checkTimer();
        checkRegistration();
        super.onResume();
    }

    @Override
    protected void onPause() {
        boolean timer_settings = encryptedSharedPreferences.getBoolean(Settings2.KEY_TIMER_SETTING, true);
        if (timer_settings)  start.cancel();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
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
                        timeUntil.setText(getResources().getString(R.string.timeUntil));
                    } else {
                        endTime = lessons[i][0] - currentTime;
                        timeUntil.setText(getResources().getString(R.string.start) + " " + romeNum[i] + " "+ getResources().getString(R.string.lesson));
                    }
                    break;
                }
            }
        } else {
            timeUntil.setText(R.string.first_lesson);
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

        boolean timer_settings = encryptedSharedPreferences.getBoolean(Settings2.KEY_TIMER_SETTING, true);
        if (!timer_settings) {
            Time.setVisibility(View.GONE);
            TimeUntil.setVisibility(View.GONE);
        } else {
            Time.setVisibility(View.VISIBLE);
            TimeUntil.setVisibility(View.VISIBLE);
            time();
        }
    }

    public void checkRegistration() {
        Boolean is_registered = encryptedSharedPreferences.getBoolean(Settings2.KEY_IS_REGISTERED, false);

        if (!is_registered) {
            Intent intent;
            intent = new Intent(this, Login.class);
            startActivity(intent);
        } else {
            return;
        }
    }

    protected void updateRating( final String NickName, final String NameDiscp, final String status, final byte IDZ)
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
                Toast.makeText(MainActivity.this, R.string.no_connection_server, Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("NickName", NickName);
                parameters.put("NameDiscp", NameDiscp);
                parameters.put("Status", status);
                parameters.put("IDZ", Byte.toString(IDZ));
                return parameters;
            }
        };
        requestQueue.add(request);
    }

    protected void loadStatusFromServer()
    {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Discipline>>() {}.getType();
        discs = gson.fromJson(JSONHelper.read(MainActivity.this, FILE_NAME), listType);

        for (int i = 0; i < discs.size(); ++i)
            getStatus(encryptedSharedPreferences.getString(Settings2.KEY_NICKNAME, ""), i);
    }

    protected void loadStatusFromDevice()
    {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Discipline>>() {}.getType();
        discs = gson.fromJson(JSONHelper.read(MainActivity.this, FILE_NAME), listType);

        for (int i = 0; i < discs.size(); ++i)
        {
            Discipline temp = discs.get(i);
            updateRating(encryptedSharedPreferences.getString(Settings2.KEY_NICKNAME, ""), temp.getName(), gson.toJson(temp.getComplete()), temp.getIDZ());
        }
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
            alertDialog.setTitle(getResources().getString(R.string.localdata_is_found) + " " + format.format(date));
        }
        else{
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Discipline>>() {}.getType();

            String test = JSONHelper.read(MainActivity.this, "data_disc.json");
            discs = gson.fromJson(test, listType);

            for (int i = 0; i < discs.size(); ++i)
                getStatus(encryptedSharedPreferences.getString(Settings2.KEY_NICKNAME, ""), i);
            return;
        }


        alertDialog.setMessage(R.string.get_data_dialog);

        alertDialog.setPositiveButton(R.string.device, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                loadStatusFromDevice();
            }
        });

        alertDialog.setNegativeButton(R.string.server, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                loadStatusFromServer();
            }
        });

        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    protected void saveJSON()
    {
        prefEditor.putBoolean(Settings2.KEY_OFFLINE_DATA, false);
        prefEditor.apply();
        Gson gson = new Gson();
        String jsonString = gson.toJson(discs);
        JSONHelper.create(MainActivity.this, FILE_NAME, jsonString);
        whole = true;
    }

    int count = 0;
    protected void getStatus (final String NickName, final int i)
    {
        String url = MainActivity.MAIN_URL + "getStatus.php";

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                if(!response.equals("null"))
                {
                    JSONObject obj;
                    try {
                         obj = new JSONObject(response);
                         byte IDZ = (byte)obj.getInt("IDZ");
                         Gson gson = new Gson();
                         boolean[][] compl = gson.fromJson(obj.getString("status"), boolean[][].class);
                         discs.get(i).setComplete(compl);
                         discs.get(i).setIDZ(IDZ);

                         compl = discs.get(0).getComplete();
                         compl[0][0] = true;

                         ++count;
                         if (count == discs.size())
                             saveJSON();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (whole) {
                    Toast.makeText(MainActivity.this, R.string.no_connection_server, Toast.LENGTH_LONG).show();
                    whole = false;
                }
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("NickName", NickName);
                parameters.put("NameDiscp", discs.get(i).getName());
                return parameters;
            }
        };
        requestQueue.add(request);
    }

    public boolean checkConnection () {
        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public void setFile(){
        String masterKeyAlias = null;
        try {
            masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            encryptedSharedPreferences = EncryptedSharedPreferences.create(
                    "secret_shared_prefs",
                    masterKeyAlias,
                    this,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //получение групп по курсу
    protected void getGroups ()
    {
        String url = MainActivity.NEW_MAIN_URL + "getGroups.php";
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                Gson gson = new Gson();
                GROUPS = gson.fromJson(response, groups[].class);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "No connection with our server,try later...", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("Course", String.valueOf(3));
                return parameters;
            }
        };
        requestQueue.add(request);
    }
}
