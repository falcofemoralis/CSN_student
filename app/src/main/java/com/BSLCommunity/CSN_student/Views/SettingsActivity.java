package com.BSLCommunity.CSN_student.Views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.BSLCommunity.CSN_student.Managers.AnimationManager;
import com.BSLCommunity.CSN_student.Managers.LocaleHelper;
import com.BSLCommunity.CSN_student.Presenters.SettingsPresenter;
import com.BSLCommunity.CSN_student.R;
import com.BSLCommunity.CSN_student.ViewInterfaces.SettingsView;

import java.util.ArrayList;

public class SettingsActivity extends BaseActivity implements SettingsDialogEditText.DialogListener, SettingsView {
    TextView nicknameText, passwordText, groupText, languageText; // поля в которых отображается информация юзера
    SettingsPresenter settingsPresenter;
    SettingsDialogEditText settingsDialogEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnimationManager.setAnimation(getWindow(), this);
        setContentView(R.layout.activity_settings);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Получение необходимых полей из активити
        nicknameText = findViewById(R.id.activity_settings_tv_nickname);
        passwordText = findViewById(R.id.activity_settings_tv_password);
        groupText = findViewById(R.id.activity_settings_tv_group);
        languageText = findViewById(R.id.activity_settings_tv_language);

        // Добавление гитхаб и телеграм профилей
        attachListeners(R.id.activity_settings_bt_dev1_github,R.id.activity_settings_bt_dev1_telegram, "https://github.com/falcofemoralis", "https://t.me/falcofemoralis");
        attachListeners(R.id.activity_settings_bt_dev2_github,R.id.activity_settings_bt_dev2_telegram, "https://github.com/Derlados", "https://t.me/Derlados");

        this.settingsPresenter = new SettingsPresenter(this);
    }

    /**
     * Обработчик нажатия на диалоговое поле
     * @param view - вью
     */
    public void OnClick(View view) {
        settingsDialogEditText.setApplyKey(view.getId());
        settingsDialogEditText.show(getSupportFragmentManager(), "DialogText");
    }

    /**
     * Кнопка выхода из аккаунта
     * @param view - вью
     */
    public void Exit(View view) {
        showLogOutDialog();
    }

    /**
     * @see SettingsDialogEditText.DialogListener
     */
    @Override
    public void applyText(String text, int applyKey) {

        if (applyKey == R.id.activity_settings_ll_nickname) {
            this.settingsPresenter.addNewValue(SettingsPresenter.DataKey.NickName, text);
        } else if (applyKey == R.id.activity_settings_ll_password) {
            this.settingsPresenter.addNewValue(SettingsPresenter.DataKey.Password, text);
        } else if (applyKey == R.id.activity_settings_ll_group) {
            // TODO смена группы пользователя
        } else if (applyKey == R.id.activity_settings_ll_language) {
            this.settingsPresenter.changeLanguage(Integer.parseInt(text));
        }

        settingsPresenter.updateData();
    }

    /**
     * @see SettingsView
     */
    @Override
    public void showToast(int id) {
        Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
    }

    /**
     * @see SettingsView
     */
    @Override
    public void openLogin() {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        Toast.makeText(SettingsActivity.this, R.string.exit, Toast.LENGTH_SHORT).show();
    }

    /**
     * @see SettingsView
     */
    @Override
    public void setDataToSettings(String nickName, String password, String group, ArrayList<Pair<String, String>> languages) {
        nicknameText.setText(nickName);
        passwordText.setText(password);
        groupText.setText(group);

        this.settingsDialogEditText = new SettingsDialogEditText(nickName, password);

        for (Pair<String,String> element : languages){
            if (element.second.contains(LocaleHelper.getLanguage(this))){
                languageText.setText(element.first);
            }
        }
    }

    /**
     * @see SettingsView
     */
    @Override
    public void updateData(String nickName, String password) {
        nicknameText.setText(nickName);
        passwordText.setText(password);
        settingsDialogEditText.updateData(nickName, password);
    }

    /**
     * @see SettingsView
     */
    @Override
    public void reloadActivity() {
        this.startActivity(new Intent(this, MainActivity.class));
        this.finishAffinity();
    }

    /**
     * Открытие диалогового окна подтверджения выхода из аккаунта
     */
    private void showLogOutDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SettingsActivity.this);

        alertDialog.setMessage(R.string.exitconfirm);
        alertDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                settingsPresenter.logOut();
                finish();
            }
        });
        alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { }
        });

        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    /**
     * Привязка обработчиков нажатий на кнопки гитхаб и телеграм профилей
     * @param github - id кнопки гитхаба
     * @param telegram - id кнопки телеграма
     * @param githubURL - ссылка на гитхаб профиль
     * @param telegramURL - ссылка на телеграм профиль
     */
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
