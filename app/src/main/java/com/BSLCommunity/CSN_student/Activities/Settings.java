package com.BSLCommunity.CSN_student.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.BSLCommunity.CSN_student.R;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

public class Settings extends AppCompatActivity implements SettingsDialogEditText.DialogListener {

    TextView nickname_setText, password_setText, group_setText;
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_GROUP = "group";
    public static final String KEY_IS_REGISTERED = "is_registered";
    public static final String KEY_NICKNAME = "nickname";
    public static final String KEY_TIMER_SETTING = "timer_setting";
    public static final String KEY_OFFLINE_DATA = "offline_data";
    SharedPreferences.Editor prefEditor;
    String oldNickname, oldPassword, oldGroup;
    String nickname, password, group;
    String URL = Main.MAIN_URL + "updateUser.php";
    RequestQueue requestQueue;
    public static SharedPreferences encryptedSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        prefEditor = encryptedSharedPreferences.edit();
        nickname_setText = (TextView) findViewById(R.id.nickname_setText);
        password_setText = (TextView) findViewById(R.id.password_setText);
        group_setText = (TextView) findViewById(R.id.group_setText);

        nickname_setText.setText(encryptedSharedPreferences.getString(KEY_NICKNAME, ""));
        password_setText.setText(encryptedSharedPreferences.getString(KEY_PASSWORD, ""));
        group_setText.setText(encryptedSharedPreferences.getString(KEY_GROUP, ""));

        nickname = encryptedSharedPreferences.getString(KEY_NICKNAME, "");
        oldNickname = nickname;
        password = encryptedSharedPreferences.getString(KEY_PASSWORD, "");
        oldPassword = password;
        group = encryptedSharedPreferences.getString(KEY_GROUP, "");
        oldGroup = group;

        Switch timerSwitch = (Switch) findViewById(R.id.timer_switch);
        if (encryptedSharedPreferences.getBoolean(Settings.KEY_TIMER_SETTING, true))
            timerSwitch.setChecked(true);
        else timerSwitch.setChecked(false);

        timerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) prefEditor.putBoolean(KEY_TIMER_SETTING, true).apply();
                else prefEditor.putBoolean(KEY_TIMER_SETTING, false).apply();
            }
        });
    }

    public void OnClick(View v) {
        SettingsDialogEditText SettingsDialogEditText = new SettingsDialogEditText(v.getId());
        SettingsDialogEditText.show(getSupportFragmentManager(), "DialogText");
    }

    public void Exit(View v) {
        showDialog();
    }

    @Override
    public void applyText(String text, int applyKey) {
        switch (applyKey) {
            case R.id.nickname:
                if (text.equals("")) {
                    Toast.makeText(Settings.this, R.string.nodata, Toast.LENGTH_SHORT).show();
                    return;
                }
                nickname_setText.setText(text);
                prefEditor.putString(KEY_NICKNAME, text).apply();
                setData();
                break;
            case R.id.password:
                if (text.equals("")) {
                    Toast.makeText(Settings.this, R.string.nodata, Toast.LENGTH_SHORT).show();
                    return;
                }
                password_setText.setText(text);
                prefEditor.putString(KEY_PASSWORD, text).apply();
                setData();
                break;
            case R.id.group:
                group_setText.setText(text);
                prefEditor.putString(KEY_GROUP, text).apply();
                setData();
                break;
        }
    }

    public void setData() {
        nickname = encryptedSharedPreferences.getString(KEY_NICKNAME, "");
        password = encryptedSharedPreferences.getString(KEY_PASSWORD, "");
        group = encryptedSharedPreferences.getString(KEY_GROUP, "");

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.indexOf("Duplicate") != -1) {
                    prefEditor.putString(KEY_NICKNAME, oldNickname);
                    prefEditor.apply();
                    nickname_setText.setText(oldNickname);
                    Toast.makeText(Settings.this, R.string.nickname_is_taken, Toast.LENGTH_SHORT).show();
                } else {
                    oldNickname = encryptedSharedPreferences.getString(Settings.KEY_NICKNAME, "");
                    oldPassword = encryptedSharedPreferences.getString(Settings.KEY_PASSWORD, "");
                    oldGroup = encryptedSharedPreferences.getString(Settings.KEY_GROUP, "");
                    Toast.makeText(Settings.this, R.string.datachanged, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(Settings.this, R.string.no_connection, Toast.LENGTH_SHORT).show();
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

    protected void showDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Settings.this);

        alertDialog.setMessage(R.string.exitconfirm);

        alertDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                prefEditor.putBoolean(KEY_IS_REGISTERED, false).apply();
                Toast.makeText(Settings.this, R.string.exit, Toast.LENGTH_SHORT).show();
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

    public static void setSettingsFile(Context context) {
        String masterKeyAlias = null;
        try {
            masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            encryptedSharedPreferences = EncryptedSharedPreferences.create(
                    "secret_shared_prefs",
                    masterKeyAlias,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
