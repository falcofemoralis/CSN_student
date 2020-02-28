package com.example.ksm_2_course;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;

import org.json.JSONObject;

import java.util.Calendar;


public class Lessons_schedule extends AppCompatActivity {

    TableLayout tableLayout_r[] = new TableLayout[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lessons_schedule);

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        tableLayout_r[0] = (TableLayout) findViewById(R.id.tableLayout_mon);
        tableLayout_r[1] = (TableLayout) findViewById(R.id.tableLayout_tue);
        tableLayout_r[2] = (TableLayout) findViewById(R.id.tableLayout_wed);
        tableLayout_r[3] = (TableLayout) findViewById(R.id.tableLayout_thur);

        if(day==2){
            tableLayout_r[0].setBackground(getResources().getDrawable(R.drawable.borders));
        }else if(day==3){
            tableLayout_r[1].setBackground(getResources().getDrawable(R.drawable.borders));
        }else if(day==4){
            tableLayout_r[2].setBackground(getResources().getDrawable(R.drawable.borders));
        }else if(day==5){
            tableLayout_r[3].setBackground(getResources().getDrawable(R.drawable.borders));
        }
        

    }

}
