package com.BSLCommunity.CSN_student.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.BSLCommunity.CSN_student.Managers.AnimationManager;
import com.BSLCommunity.CSN_student.Objects.Groups;
import com.BSLCommunity.CSN_student.Objects.Settings;
import com.BSLCommunity.CSN_student.Objects.Subjects;
import com.BSLCommunity.CSN_student.Objects.SubjectsInfo;
import com.BSLCommunity.CSN_student.Objects.User;
import com.BSLCommunity.CSN_student.R;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import static com.BSLCommunity.CSN_student.Objects.Settings.encryptedSharedPreferences;

public class SettingsActivity extends AppCompatActivity implements SettingsDialogEditText.DialogListener {
    SharedPreferences.Editor prefEditor; //локальные данные
    TextView nicknameText, passwordText, groupText; // поля в которых отображается информация юзера

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
            case R.id.activity_settings_ll_group:
                break;
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
}
