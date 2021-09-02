package com.BSLCommunity.CSN_student.Models;

import com.BSLCommunity.CSN_student.Constants.LogType;

import java.util.Calendar;

public class UserLog {
    public int type;
    public String info;
    public long time;

    public UserLog(LogType type) {
        this.type = type.ordinal();
        init();
    }

    public UserLog(LogType type, String info) {
        this.type = type.ordinal();
        this.info = info;
        init();
    }

    public void init() {
        this.time = Calendar.getInstance().getTimeInMillis();
    }
}
