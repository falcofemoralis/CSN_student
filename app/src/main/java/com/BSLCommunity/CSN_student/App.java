package com.BSLCommunity.CSN_student;

import android.app.Application;
import android.content.Context;

public class App extends Application {
    private static App instance = null;

    public static App getApp() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

    }

    public Context context() {
        return this.getApplicationContext();
    }
}
