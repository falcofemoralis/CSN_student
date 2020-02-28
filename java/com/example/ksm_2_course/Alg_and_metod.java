package com.example.ksm_2_course;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.os.Bundle;
import android.view.ViewDebug;
import android.widget.Button;

public class Alg_and_metod extends AppCompatActivity {

    Button res;
    SharedPreferences Pref;
    Button buts[][] = new Button[6][2];
    int complete = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alg_and_metod);

        res = (Button) findViewById(R.id.res);
        buts[0][0] = (Button)(findViewById(R.id.B_0_0));
        buts[0][1] = (Button)(findViewById(R.id.B_0_1));
        buts[1][0] = (Button)(findViewById(R.id.B_1_0));
        buts[1][1] = (Button)(findViewById(R.id.B_1_1));
        buts[2][0] = (Button)(findViewById(R.id.B_2_0));
        buts[2][1] = (Button)(findViewById(R.id.B_2_1));
        buts[3][0] = (Button)(findViewById(R.id.B_3_0));
        buts[3][1] = (Button)(findViewById(R.id.B_3_1));
        buts[4][0] = (Button)(findViewById(R.id.B_4_0));
        buts[4][1] = (Button)(findViewById(R.id.B_4_1));
        buts[5][0] = (Button)(findViewById(R.id.B_5_0));
        buts[5][1] = (Button)(findViewById(R.id.B_5_1));
        RestoreAll();
    }

    protected void RestoreAll()
    {
        Pref = getSharedPreferences("RESAlg", MODE_PRIVATE);

        if (Pref.getInt("LabAlg_" + Integer.toString(1) + "_0", -1) == -1)
        {
            Editor ed = Pref.edit();
            for (int i = 0; i < 6; ++i) {
                ed.putInt("LabAlg_" + Integer.toString(i) + "_0", 0);
                ed.putInt("LabAlg_" + Integer.toString(i) + "_1", 0);
            }
            ed.commit();
        }
        else
        for (int i = 0; i < 6; ++i)
        {
            if (Pref.getInt("LabAlg_" + Integer.toString(i) + "_0", -1) == 1) {
                buts[i][0].setBackgroundColor(0xFFDFFFBF);
                ++complete;
            }
            else
                buts[i][0].setBackgroundColor(0xFFF56D6D);

            if (Pref.getInt("LabAlg_" + Integer.toString(i) + "_1", -1) == 1) {
                buts[i][1].setBackgroundColor(0xFFDFFFBF);
                ++complete;
            }
            else
                buts[i][1].setBackgroundColor(0xFFF56D6D);
        }
        res.setText(Integer.toString(complete * 100 / 12) + "%");
    }

    @Override
    protected  void onDestroy()
    {
        super.onDestroy();
        SaveAll();
    }

    protected void SaveAll()
    {
        Pref = getSharedPreferences("RESAlg", MODE_PRIVATE);
        Editor ed = Pref.edit();

        for (int i = 0; i < 6; ++i)
        {
            if (((ColorDrawable)buts[i][0].getBackground()).getColor() == 0xFFDFFFBF)
                ed.putInt("LabAlg_" + Integer.toString(i) + "_0", 1);
            else
                ed.putInt("LabAlg_" + Integer.toString(i) + "_0", 0);

            if (((ColorDrawable)buts[i][1].getBackground()).getColor() == 0xFFDFFFBF)
                ed.putInt("LabAlg_" + Integer.toString(i) + "_1", 1);
            else
                ed.putInt("LabAlg_" + Integer.toString(i) + "_1", 0);
        }
        ed.putInt("RESAlg", complete * 100 / 12);
        ed.commit();

    }

    public void OnClick(View v) {
        Button but = (Button) v;

        if (((ColorDrawable) but.getBackground()).getColor() == 0xFFF56D6D)
        {
            ++complete;
            but.setBackgroundColor(0xFFDFFFBF);
        } else
        {
            --complete;
            but.setBackgroundColor(0xFFF56D6D);
        }

        res.setText(Integer.toString(complete * 100 / 12) + "%");
    }
}
