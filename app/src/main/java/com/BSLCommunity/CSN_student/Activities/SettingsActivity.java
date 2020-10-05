package com.BSLCommunity.CSN_student.Activities;

import android.app.AlertDialog;
import android.app.Application;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.IntentCompat;

import com.BSLCommunity.CSN_student.Managers.AnimationManager;
import com.BSLCommunity.CSN_student.Managers.LocaleHelper;
import com.BSLCommunity.CSN_student.Objects.Groups;
import com.BSLCommunity.CSN_student.Objects.Settings;
import com.BSLCommunity.CSN_student.Objects.Subjects;
import com.BSLCommunity.CSN_student.Objects.SubjectsInfo;
import com.BSLCommunity.CSN_student.Objects.Teachers;
import com.BSLCommunity.CSN_student.Objects.User;
import com.BSLCommunity.CSN_student.R;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import static com.BSLCommunity.CSN_student.Objects.Settings.encryptedSharedPreferences;
import static com.BSLCommunity.CSN_student.Objects.Settings.languages;

public class SettingsActivity extends BaseActivity implements SettingsDialogEditText.DialogListener {
    SharedPreferences.Editor prefEditor; //локальные данные
    TextView nicknameText, passwordText, groupText, languageText; // поля в которых отображается информация юзера

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AnimationManager.setAnimation(getWindow(), this);
        setContentView(R.layout.activity_settings);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //получаем необходимые объекты
        prefEditor = encryptedSharedPreferences.edit();
        nicknameText = (TextView) findViewById(R.id.activity_settings_tv_nickname);
        passwordText = (TextView) findViewById(R.id.activity_settings_tv_password);
        groupText = (TextView) findViewById(R.id.activity_settings_tv_group);
        languageText = (TextView) findViewById(R.id.activity_settings_tv_language);

        //добвляем профиля
        attachListeners(R.id.activity_settings_bt_dev1_github,R.id.activity_settings_bt_dev1_telegram, "https://github.com/falcofemoralis", "https://t.me/falcofemoralis");
        attachListeners(R.id.activity_settings_bt_dev2_github,R.id.activity_settings_bt_dev2_telegram, "https://github.com/Derlados", "https://t.me/Derlados");

        //добавляем языки
        String[] languagesArray = getResources().getStringArray(R.array.languages);
        languages.add(new Pair<String, String>(languagesArray[0],"en"));
        languages.add(new Pair<String, String>(languagesArray[1],"ru"));
        languages.add(new Pair<String, String>(languagesArray[2],"uk"));

        //устанавливаем данные
        updateViewTexts();
        setTimer();
    }

    //обработчик кнопки переключения видимости таймера
    public void setTimer(){
        //получаем переключатель таймера
        Switch timerSwitch = (Switch) findViewById(R.id.activity_settings_sw_timer);

        //устанавливаем изначальное значение
        if (encryptedSharedPreferences.getBoolean(Settings.PrefKeys.TIMER_SWITCH.getKey(), true))
            timerSwitch.setChecked(true);
        else timerSwitch.setChecked(false);

        //добавляем листенер кнопке таймеру
        timerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) prefEditor.putBoolean(Settings.PrefKeys.TIMER_SWITCH.getKey(), true).apply();
                else prefEditor.putBoolean(Settings.PrefKeys.TIMER_SWITCH.getKey(), false).apply();
            }
        });
    }

    //обработчик нажатия на диалоговое поле
    public void OnClick(View view) {
        SettingsDialogEditText SettingsDialogEditText = new SettingsDialogEditText(view.getId());
        SettingsDialogEditText.show(getSupportFragmentManager(), "DialogText");
    }

    public void Exit(View view) {
        showDialog();
    }

    @Override
    public void applyText(String text, int applyKey) {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("NickName", encryptedSharedPreferences.getString(Settings.PrefKeys.NICKNAME.getKey(), ""));
        parameters.put("Password", encryptedSharedPreferences.getString(Settings.PrefKeys.PASSWORD.getKey(),""));

        switch (applyKey) {
            case R.id.activity_settings_ll_nickname:
                if (text.equals("")) {
                    Toast.makeText(SettingsActivity.this, R.string.nodata, Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    parameters.put("NickName", text);
                }
                break;
            case R.id.activity_settings_ll_password:
                if (text.equals("")) {
                    Toast.makeText(SettingsActivity.this, R.string.nodata, Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    parameters.put("Password", text);
                }
                break;
            case R.id.activity_settings_ll_group: break;
            case R.id.activity_settings_ll_language:
                LocaleHelper.setLocale(this, languages.get(Integer.parseInt(text)).second);
                updateViewTexts();
                int size = languages.size();
                for(int i=0;i<size;i++)
                   languages.remove(0);
                this.startActivity(new Intent(this, MainActivity.class));
                this.finishAffinity();
                return;
        }
        try {
            User.getInstance().update(getApplicationContext(), this, parameters, new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    updateViewTexts();
                    return null;
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void updateViewTexts() {
        nicknameText.setText(encryptedSharedPreferences.getString(Settings.PrefKeys.NICKNAME.getKey(), ""));
        passwordText.setText(encryptedSharedPreferences.getString(Settings.PrefKeys.PASSWORD.getKey(), ""));
        groupText.setText(encryptedSharedPreferences.getString(Settings.PrefKeys.GROUP.getKey(), ""));

        for (Pair<String,String> element : languages){
            if (element.second.contains(LocaleHelper.getLanguage(this))){
                languageText.setText(element.first);
            }
        }
    }

    protected void showDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SettingsActivity.this);

        alertDialog.setMessage(R.string.exitconfirm);

        alertDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                prefEditor.putBoolean(Settings.PrefKeys.IS_REGISTERED.getKey(), false).apply();
                // Удаление данных
                User.deleteUser();
                SubjectsInfo.deleteSubjects(getApplicationContext());
                Groups.delete(getApplicationContext());
                Subjects.delete(getApplicationContext());
                Teachers.delete(getApplicationContext());

                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                Toast.makeText(SettingsActivity.this, R.string.exit, Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void attachListeners(int github, int telegram, final String githubURL, final String telegramURL){
        Button githubBtn = findViewById(github);
        Button telegramBtn = findViewById(telegram);

        githubBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent linkIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(githubURL));
                startActivity(linkIntent);
            }
        });
        telegramBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent linkIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(telegramURL));
                startActivity(linkIntent);
            }
        });
    }
}
