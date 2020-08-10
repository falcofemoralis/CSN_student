package com.BSLCommunity.CSN_student.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.BSLCommunity.CSN_student.Managers.JSONHelper;
import com.BSLCommunity.CSN_student.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Subjects extends AppCompatActivity {

    ArrayList<Discipline> discs = new ArrayList<Discipline>(); //Дисциплины

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjects);
    }

    @Override
    protected void onResume() {
        setProgress();
        super.onResume();
    }

    public void OnClick(View v) {
        Animation click = AnimationUtils.loadAnimation(this, R.anim.btn_click);
        v.startAnimation(click);
        Intent intent;
        intent = new Intent(this, Disciplines.class);
        intent.putExtra("button_id", v.getId());
        startActivity(intent);
        setProgress();
    }

    public void setProgress() {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Discipline>>() {
        }.getType();
        discs = gson.fromJson(JSONHelper.read(this, Main.FILE_NAME), listType);

        int sum = 0, all = 0;
        for (int i = 0; i < discs.size(); ++i) {
            Discipline temp = discs.get(i);

            boolean[][] temp_bool = temp.getComplete();
            for (int j = 0; j < temp_bool.length; ++j) {
                sum += temp_bool[j][0] ? 1 : 0;
                sum += temp_bool[j][1] ? 1 : 0;
            }
            all += temp.getLabs();
        }
        all *= 2;

        ((Button) findViewById(R.id.res)).setText(Integer.toString(sum * 100 / all) + "%");
    }
}
