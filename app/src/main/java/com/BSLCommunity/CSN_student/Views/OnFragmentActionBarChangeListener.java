package com.BSLCommunity.CSN_student.Views;

import com.BSLCommunity.CSN_student.Constants.ActionBarType;

public interface OnFragmentActionBarChangeListener {
    void changeActionBarState(boolean state);

    void setActionBarColor(int color, ActionBarType type);
}