package com.BSLCommunity.CSN_student.Models;

import android.app.Application;
import android.content.Context;
import com.BSLCommunity.CSN_student.Managers.LocaleHelper;

import java.util.Locale;

public class App extends Application {
    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(LocaleHelper.onAttach(context, Locale.getDefault().getLanguage()));

    }
}