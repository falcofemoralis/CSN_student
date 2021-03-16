package com.BSLCommunity.CSN_student.Views.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.BSLCommunity.CSN_student.Managers.LocaleHelper;
import com.BSLCommunity.CSN_student.Presenters.SettingsPresenter;
import com.BSLCommunity.CSN_student.R;
import com.BSLCommunity.CSN_student.ViewInterfaces.SettingsView;
import com.BSLCommunity.CSN_student.Views.OnFragmentInteractionListener;

import java.util.ArrayList;

public class SettingsFragment extends Fragment implements SettingsDialogEditText.DialogListener, SettingsView {
    TextView nicknameText, passwordText, groupText, languageText; // поля в которых отображается информация юзера
    SettingsPresenter settingsPresenter;
    SettingsDialogEditText settingsDialogEditText;

    View currentFragment;
    OnFragmentInteractionListener fragmentListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentListener = (OnFragmentInteractionListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        currentFragment = inflater.inflate(R.layout.fragment_settings, container, false);
        // Получение необходимых полей из активити
        nicknameText = currentFragment.findViewById(R.id.activity_settings_tv_nickname);
        passwordText = currentFragment.findViewById(R.id.activity_settings_tv_password);
        groupText = currentFragment.findViewById(R.id.activity_settings_tv_group);
        languageText = currentFragment.findViewById(R.id.activity_settings_tv_language);

        // Добавление гитхаб и телеграм профилей
        attachListeners(R.id.activity_settings_bt_dev1_github,R.id.activity_settings_bt_dev1_telegram, "https://github.com/falcofemoralis", "https://t.me/falcofemoralis");
        attachListeners(R.id.activity_settings_bt_dev2_github,R.id.activity_settings_bt_dev2_telegram, "https://github.com/Derlados", "https://t.me/Derlados");

        this.settingsPresenter = new SettingsPresenter(this);
        return currentFragment;
    }

    /**
     * Обработчик нажатия на диалоговое поле
     * @param view - вью
     */
    public void OnClick(View view) {
        settingsDialogEditText.setApplyKey(view.getId());
        settingsDialogEditText.show(getActivity().getSupportFragmentManager(), "DialogText");
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
        Toast.makeText(getContext(), id, Toast.LENGTH_SHORT).show();
    }

    /**
     * @see SettingsView
     */
    @Override
    public void openLogin() {
        fragmentListener.onFragmentInteraction(this, new LoginFragment(),
                OnFragmentInteractionListener.Action.NEXT_FRAGMENT_HIDE, null, null);
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
            if (element.second.contains(LocaleHelper.getLanguage(getContext()))){
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
//        this.startActivity(new Intent(this, MainActivity.class));
//        this.finishAffinity();
    }

    /**
     * Открытие диалогового окна подтверджения выхода из аккаунта
     */
    private void showLogOutDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());

        alertDialog.setMessage(R.string.exitconfirm);
        alertDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                settingsPresenter.logOut();
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
        Button githubBtn = currentFragment.findViewById(github);
        Button telegramBtn = currentFragment.findViewById(telegram);

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
