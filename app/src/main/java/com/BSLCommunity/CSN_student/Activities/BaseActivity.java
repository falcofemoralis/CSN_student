package com.BSLCommunity.CSN_student.Activities;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import com.BSLCommunity.CSN_student.Managers.LocaleHelper;

import java.util.Locale;

public class BaseActivity extends AppCompatActivity{
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}
