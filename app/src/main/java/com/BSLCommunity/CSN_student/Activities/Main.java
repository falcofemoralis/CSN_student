package com.BSLCommunity.CSN_student.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.BSLCommunity.CSN_student.Managers.JSONHelper;
import com.BSLCommunity.CSN_student.Objects.Timer;
import com.BSLCommunity.CSN_student.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.BSLCommunity.CSN_student.Activities.Disciplines.discs;
import static com.BSLCommunity.CSN_student.Activities.Settings.encryptedSharedPreferences;
import static com.BSLCommunity.CSN_student.Objects.User.getGroups;

public class Main extends AppCompatActivity {
    public static String MAIN_URL = "http://192.168.1.3";

    Timer timer = new Timer(); //таймер
    TextView Time, TimeUntil; //переменные таймера

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Time = (TextView) findViewById(R.id.Time);
        TimeUntil = (TextView) findViewById(R.id.timeUntil);

        Settings.setSettingsFile(this);

        checkData();
        getGroups(this);
    }

    public void OnClick(View view) {
        Animation click = AnimationUtils.loadAnimation(this, R.anim.btn_click);
        view.startAnimation(click);

        Class activity = null;
        switch (view.getId()) {
            case R.id.subjectsBtn:
                activity = Subjects.class;
                break;
            case R.id.ratingBtn:
                activity = Rating.class;
                break;
            case R.id.lessons_scheduleBtn:
                activity = Schedule.class;
                break;
            case R.id.settingsBtn:
                activity = Settings.class;
                break;
        }
        startActivity(new Intent(this, activity));
    }

    @Override
    protected void onResume() {
        super.onResume();
        timer.checkTimer(TimeUntil, Time, getResources());

        Boolean is_registered = encryptedSharedPreferences.getBoolean(Settings.KEY_IS_REGISTERED, false);
        if (!is_registered) startActivity(new Intent(this, Login.class));
        else return;
    }

    @Override
    protected void onPause() {
        timer.resetTimer();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void checkData() {
        boolean offline_data = encryptedSharedPreferences.getBoolean(Settings.KEY_OFFLINE_DATA, false);
        if (checkConnection()) {
            if (offline_data)
                showDialog();
            else
                loadStatusFromServer();
        } else
            loadStatusFromDevice();
    }

    protected void showDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Main.this);

        String PATH = this.getFileStreamPath(Registration.FILE_NAME).toString();
        File file = new File(PATH);
        if (file.exists()) {
            long last = file.lastModified();
            Date date = new Date(last);
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy, HH:mm:ss");
            alertDialog.setTitle(getResources().getString(R.string.localdata_is_found) + " " + format.format(date));
        } else {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Discipline>>() {
            }.getType();

            String test = JSONHelper.read(Main.this, "data_disc.json");
            discs = gson.fromJson(test, listType);

            for (int i = 0; i < discs.size(); ++i)
                Disciplines.getStatus(encryptedSharedPreferences.getString(Settings.KEY_NICKNAME, ""), i, this);
            return;
        }

        alertDialog.setMessage(R.string.get_data_dialog);

        alertDialog.setPositiveButton(R.string.device, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                loadStatusFromDevice();
            }
        });

        alertDialog.setNegativeButton(R.string.server, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                loadStatusFromServer();
            }
        });

        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    protected boolean checkConnection() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    protected void loadStatusFromServer() {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Discipline>>() {
        }.getType();
        discs = gson.fromJson(JSONHelper.read(Main.this, Registration.FILE_NAME), listType);

        for (int i = 0; i < discs.size(); ++i)
            Disciplines.getStatus(encryptedSharedPreferences.getString(Settings.KEY_NICKNAME, ""), i, this);
    }

    protected void loadStatusFromDevice() {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Discipline>>() {
        }.getType();
        discs = gson.fromJson(JSONHelper.read(Main.this, Registration.FILE_NAME), listType);

        for (int i = 0; i < discs.size(); ++i) {
            Discipline temp = discs.get(i);
            Disciplines.updateRating(encryptedSharedPreferences.getString(Settings.KEY_NICKNAME, ""), temp.getName(), gson.toJson(temp.getComplete()), temp.getIDZ(), this);
        }
    }
}
