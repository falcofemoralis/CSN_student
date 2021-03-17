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
import com.BSLCommunity.CSN_student.R;
import com.BSLCommunity.CSN_student.Views.OnFragmentActionBarChangeListener;

public class ScheduleBell extends Fragment {

    public static String[][] times = {{"8:30", "9:50"}, {"10:05", "11:25"}, {"11:55", "13:15"}, {"13:25", "14:45"}, {"14:55", "16:15"}, {"16:45", "18:05"}, {"18:15", "19:35"}, {"19:45", "21:05"}};
    View currentFragment;
    OnFragmentActionBarChangeListener onFragmentActionBarChangeListener;

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
            for (int j = 1; j < tr.getChildCount(); ++j)
                ((TextView) tr.getChildAt(j)).setText(times[i - 1][j - 1]);
        }

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
}