package com.example.ksm_2_course;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import static com.example.ksm_2_course.MainActivity.encryptedSharedPreferences;

public class Settings2 extends AppCompatActivity implements SettingsDialogEditText.DialogListener{

    TextView nickname_setText,password_setText,group_setText;
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_GROUP = "group";
    public static final String KEY_IS_REGISTERED = "is_registered";
    public static final String KEY_NICKNAME = "nickname";
    public static final String KEY_TIMER_SETTING = "timer_setting";
    SharedPreferences.Editor prefEditor;
    String oldNickname,oldPassword,oldGroup;
    String nickname,password,group;
    String URL = MainActivity.MAIN_URL + "updateUser.php";
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        prefEditor = encryptedSharedPreferences.edit();
        nickname_setText = (TextView) findViewById(R.id.nickname_setText);
        password_setText = (TextView) findViewById(R.id.password_setText);
        group_setText = (TextView) findViewById(R.id.group_setText);

        nickname_setText.setText(MainActivity.encryptedSharedPreferences.getString(KEY_NICKNAME, ""));
        password_setText.setText(MainActivity.encryptedSharedPreferences.getString(KEY_PASSWORD, ""));
        group_setText.setText(MainActivity.encryptedSharedPreferences.getString(KEY_GROUP, ""));

        nickname = MainActivity.encryptedSharedPreferences.getString(KEY_NICKNAME, "");
        oldNickname = nickname;
        password = MainActivity.encryptedSharedPreferences.getString(KEY_PASSWORD, "");
        oldPassword = password;
        group = MainActivity.encryptedSharedPreferences.getString(KEY_GROUP, "");
        oldGroup = group;

        Switch timerSwitch = (Switch) findViewById(R.id.timer_switch);
        if(MainActivity.encryptedSharedPreferences.getBoolean(Settings2.KEY_TIMER_SETTING, true)) timerSwitch.setChecked(true);
        else timerSwitch.setChecked(false);

        timerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)  prefEditor.putBoolean(KEY_TIMER_SETTING, true).apply();
                else prefEditor.putBoolean(KEY_TIMER_SETTING, false).apply();
            }
        });
    }

    public void OnClick(View v) {
        SettingsDialogEditText SettingsDialogEditText = new SettingsDialogEditText(v.getId());
        SettingsDialogEditText.show(getSupportFragmentManager(),"DialogText");
    }

    public void Exit(View v) {
        prefEditor.putBoolean(KEY_IS_REGISTERED, false).apply();
        Toast.makeText(Settings2.this, R.string.exit, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void applyText(String text,int applyKey) {
        switch (applyKey){
            case R.id.nickname:
                if(text.equals("")){
                    Toast.makeText(Settings2.this, R.string.nodata, Toast.LENGTH_SHORT).show();
                    return;
                }
                nickname_setText.setText(text);
                prefEditor.putString(KEY_NICKNAME,text).apply();
                setData();
                break;
            case R.id.password:
                if(text.equals("")){
                    Toast.makeText(Settings2.this, R.string.nodata, Toast.LENGTH_SHORT).show();
                    return;
                }
                password_setText.setText(text);
                prefEditor.putString(KEY_PASSWORD,text).apply();
                setData();
                break;
            case R.id.group:
                group_setText.setText(text);
                prefEditor.putString(KEY_GROUP,text).apply();
                setData();
                break;
        }
    }

    public void setData() {
        nickname = MainActivity.encryptedSharedPreferences.getString(KEY_NICKNAME, "");
        password = MainActivity.encryptedSharedPreferences.getString(KEY_PASSWORD, "");
        group = MainActivity.encryptedSharedPreferences.getString(KEY_GROUP, "");

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.indexOf("Duplicate") != -1) {
                    prefEditor.putString(KEY_NICKNAME, oldNickname);
                    prefEditor.apply();
                    nickname_setText.setText(oldNickname);
                    Toast.makeText(Settings2.this, R.string.nickname_is_taken, Toast.LENGTH_SHORT).show();
                } else {
                    oldNickname =  MainActivity.encryptedSharedPreferences.getString(Settings2.KEY_NICKNAME, "");
                    oldPassword =  MainActivity.encryptedSharedPreferences.getString(Settings2.KEY_PASSWORD, "");
                    oldGroup =  MainActivity.encryptedSharedPreferences.getString(Settings2.KEY_GROUP, "");
                    Toast.makeText(Settings2.this, R.string.datachanged, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                prefEditor.putString(KEY_NICKNAME, oldNickname);
                prefEditor.putString(KEY_PASSWORD, oldPassword);
                prefEditor.putString(KEY_GROUP, oldGroup);
                prefEditor.apply();
                nickname_setText.setText(oldNickname);
                password_setText.setText(oldPassword);
                group_setText.setText(oldGroup);
                Toast.makeText(Settings2.this, R.string.no_connection, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("NewNickName", nickname.toLowerCase());
                parameters.put("Password", password);
                parameters.put("NameGroup", group);
                parameters.put("OldNickName", oldNickname.toLowerCase());
                return parameters;
            }
        };
        requestQueue.add(request);
    }
}
