package com.BSLCommunity.CSN_student.Views;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.BSLCommunity.CSN_student.Managers.LocaleHelper;

public class BaseActivity extends AppCompatActivity{
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}
