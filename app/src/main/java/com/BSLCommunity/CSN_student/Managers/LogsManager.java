package com.BSLCommunity.CSN_student.Managers;

import com.BSLCommunity.CSN_student.Constants.LogType;
import com.BSLCommunity.CSN_student.Models.UserLog;

import java.util.ArrayList;

public class LogsManager {
    private static LogsManager instance;
    private ArrayList<UserLog> userLogs;

    private LogsManager() {
    }

    public static LogsManager getInstance() {
        if (instance == null) {
            instance = new LogsManager();
            instance.init();
        }
        return instance;
    }

    public void init() {
        userLogs = new ArrayList<>();
    }

    public void updateLogs(LogType type) {
        userLogs.add(new UserLog(type));
    }

    public void updateLogs(LogType type, String info) {
        userLogs.add(new UserLog(type, info));
    }

    public ArrayList<UserLog> getLogs() {
        return userLogs;
    }

    public void clearLogs() {
        userLogs.clear();
    }
}
