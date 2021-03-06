package com.BSLCommunity.CSN_student;

import android.app.Application;

public class App extends Application {

    private static App instance;
    public static App getApp() { return instance; }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
