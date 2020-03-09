package com.example.ksm_2_course;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    public static final String KEY_PASSWORD = "password";
    public static final String KEY_GROUP = "group";
    public static final String KEY_IS_REGISTERED = "is_registered";
    public static final String KEY_NICKNAME = "nickname";
    public static final String KEY_TIMER_SETTING = "timer_setting";
    String url = "http://192.168.0.105/registr/Rating/updateUser.php";
    RequestQueue requestQueue;
    String oldNickname;
    SharedPreferences pref;
    String nickname,password,group;
    SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
            if(key.equals(KEY_NICKNAME)){
                nickname = sharedPreferences.getString(SettingsActivity.KEY_NICKNAME, "");
                setData();
            }else if(key.equals(KEY_PASSWORD)){
                password = sharedPreferences.getString(SettingsActivity.KEY_PASSWORD, "");
                setData();
            }else if(key.equals(KEY_GROUP)){
                group = sharedPreferences.getString(SettingsActivity.KEY_GROUP, "");
                setData();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
        oldNickname = sharedPreferences.getString(SettingsActivity.KEY_NICKNAME,"");
        nickname = sharedPreferences.getString(SettingsActivity.KEY_NICKNAME, "");
        password = sharedPreferences.getString(SettingsActivity.KEY_PASSWORD, "");
        group = sharedPreferences.getString(SettingsActivity.KEY_GROUP, "");
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }

    @Override
    protected void onResume() {
        pref.registerOnSharedPreferenceChangeListener(listener);
        super.onResume();
    }

    public void setData() {
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
                SharedPreferences.Editor pref = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this).edit();
                if (response.indexOf("Duplicate") != -1) {
                    pref.putString(SettingsActivity.KEY_NICKNAME, oldNickname);
                    pref.commit();
                    Toast.makeText(SettingsActivity.this, "This nickname is taken by another user", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "UserData changed", Toast.LENGTH_SHORT).show();
                    oldNickname = sharedPreferences.getString(SettingsActivity.KEY_NICKNAME, "");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                SharedPreferences.Editor pref = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this).edit();
                pref.putString(SettingsActivity.KEY_NICKNAME, oldNickname);
                pref.commit();
                Toast.makeText(SettingsActivity.this, "No connection", Toast.LENGTH_SHORT).show();
                finish();
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

