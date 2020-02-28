package com.example.ksm_2_course;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import java.util.Calendar;
import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class MainActivity extends AppCompatActivity {

    Button res;
    int RES,first_day=3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        res = (Button) findViewById(R.id.res);
        SetText();
    }

    public void SetText()
    {
        RES = getSharedPreferences("RESAlg", MODE_PRIVATE).getInt("RESAlg", 0)
                + getSharedPreferences("RESArch", MODE_PRIVATE).getInt("RESArch", 0)
                + getSharedPreferences("RESCS", MODE_PRIVATE).getInt("RESCS", 0)
                + getSharedPreferences("RESDB", MODE_PRIVATE).getInt("RESDB", 0)
                + getSharedPreferences("RESSMP", MODE_PRIVATE).getInt("RESSMP", 0)
                + getSharedPreferences("RESOBG", MODE_PRIVATE).getInt("RESOBG", 0)
        ;
        res.setText(Integer.toString(RES / 6) + "%");
    }

    public void OnClick(View v)
    {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        Intent intent;
        switch ((v.getId()))
        {
            case R.id.Alg:
                intent = new Intent(this, Alg_and_metod.class);
                startActivity(intent);
                break;
            case R.id.Arch:
                intent = new Intent(this, Arch_Comp.class);
                startActivity(intent);
                break;
            case R.id.CS:
                intent = new Intent(this, CompScheme.class);
                startActivity(intent);
                break;
            case R.id.OBD:
                intent = new Intent(this, DataBase.class);
                startActivity(intent);
                break;
            case R.id.OBG:
                intent = new Intent(this, OBG.class);
                startActivity(intent);
                break;
            case R.id.SMP:
                intent = new Intent(this, S_metod_prog.class);
                startActivity(intent);
                break;
            case R.id.LS:
                if(((day-first_day)/7)%2==0){
                    intent = new Intent(this, Lessons_schedule.class);
                    startActivity(intent);
                }else{
                    intent = new Intent(this, Lessons_schedule_2.class);
                    startActivity(intent);
                }

                break;
        }
        SetText();
    }
}

