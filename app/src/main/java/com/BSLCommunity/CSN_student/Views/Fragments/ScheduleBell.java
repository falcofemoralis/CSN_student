package com.BSLCommunity.CSN_student.Views.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.BSLCommunity.CSN_student.Constants.ActionBarType;
import com.BSLCommunity.CSN_student.Models.Entity.Timer;
import com.BSLCommunity.CSN_student.R;
import com.BSLCommunity.CSN_student.Views.OnFragmentActionBarChangeListener;

public class ScheduleBell extends Fragment implements Timer.ITimer {
    View currentFragment;
    OnFragmentActionBarChangeListener onFragmentActionBarChangeListener;
    TextView schedulePairs[][] = new TextView[5][2];

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onFragmentActionBarChangeListener = (OnFragmentActionBarChangeListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        currentFragment = inflater.inflate(R.layout.fragment_schedule_bell, container, false);
        TableLayout tl = currentFragment.findViewById(R.id.activity_schedule_bell_tl_schedule);

        for (int i = 1; i < tl.getChildCount(); ++i) {
            TableRow tr = (TableRow) tl.getChildAt(i);

            for (int j = 0; j < tr.getChildCount(); ++j) {
                if (j == 0) {
                    schedulePairs[i - 1][0] = (TextView) tr.getChildAt(j);
                    continue;
                }

                if (j == tr.getChildCount() - 1) {
                    TextView breakTV = (TextView) tr.getChildAt(j);
                    breakTV.setText(Timer.breaks[i - 1]);
                    schedulePairs[i - 1][1] = breakTV;
                    continue;
                }

                ((TextView) tr.getChildAt(j)).setText(Timer.times[i - 1][j - 1]);
            }
        }

        new Timer(this);

        return currentFragment;
    }

    @Override
    public void onResume() {
        onFragmentActionBarChangeListener.setActionBarColor(R.color.dark_blue, ActionBarType.STATUS_BAR);
        super.onResume();
    }

    @Override
    public void onPause() {
        onFragmentActionBarChangeListener.setActionBarColor(R.color.background, ActionBarType.STATUS_BAR);
        super.onPause();
    }

    @Override
    public void updateTime(String time) {

    }

    @Override
    public void updatePair(int pair, Timer.TimeType type) {
        if (type == Timer.TimeType.UNTIL_START && pair != 0) {
            schedulePairs[pair - 1][1].setBackgroundColor(getResources().getColor(R.color.main_color_2));
        } else if (type == Timer.TimeType.UNTIL_END) {
            schedulePairs[pair][0].setBackgroundColor(getResources().getColor(R.color.main_color_2));
        }
    }
}