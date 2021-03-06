package com.BSLCommunity.CSN_student.Views;

import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.BSLCommunity.CSN_student.R;

public class ScheduleBell extends BaseActivity {

    public static String[][] times = {{"8:30", "9:50"}, {"10:05", "11:25"}, {"11:55", "13:15"}, {"13:25", "14:45"}, {"14:55", "16:15"}, {"16:45", "18:05"}, {"18:15", "19:35"}, {"19:45", "21:05"}};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_bell);

        TableLayout tl = findViewById(R.id.activity_schedule_bell_tl_schedule);

        for (int i = 1; i < tl.getChildCount(); ++i)
        {
            TableRow tr = (TableRow) tl.getChildAt(i);
            for (int j = 1; j < tr.getChildCount(); ++j)
                ((TextView)tr.getChildAt(j)).setText(times[i - 1][j - 1]);
        }
    }
}