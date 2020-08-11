package com.BSLCommunity.CSN_student.Objects;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class Settings {

    // Строковые константы - ключи, по которым хранятся все данные в файле настроек
    public enum PrefKeys {
        USER_ID("user_id"),
        NICKNAME("nickname"),
        PASSWORD("password"),
        GROUP("group"),
        GROUP_ID("group_id"),
        COURSE("course"),
        IS_REGISTERED("is_registered"),
        OFFLINE_DATA("offline_data");

        private String value;
        private PrefKeys(String value) {
            this.value = value;
        }

        public String getKey() {
            return value;
        }
    }

    public static SharedPreferences encryptedSharedPreferences;

    public static void setSettingsFile(Context context) {
        MasterKey  masterKey = null;
        try {
            masterKey = new MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            encryptedSharedPreferences = EncryptedSharedPreferences.create(context,"settings_data",masterKey, EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
