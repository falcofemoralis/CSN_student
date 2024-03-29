package com.BSLCommunity.CSN_student.Views.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.BSLCommunity.CSN_student.App;
import com.BSLCommunity.CSN_student.Constants.LogType;
import com.BSLCommunity.CSN_student.Managers.LocaleHelper;
import com.BSLCommunity.CSN_student.Managers.LogsManager;
import com.BSLCommunity.CSN_student.Presenters.SettingsPresenter;
import com.BSLCommunity.CSN_student.R;
import com.BSLCommunity.CSN_student.ViewInterfaces.SettingsView;
import com.BSLCommunity.CSN_student.Views.MainActivity;
import com.BSLCommunity.CSN_student.Views.OnFragmentInteractionListener;
import com.chivorn.smartmaterialspinner.SmartMaterialSpinner;

import java.util.ArrayList;
import java.util.Arrays;

public class SettingsFragment extends Fragment implements SettingsView, SettingsDialogEditText.DialogListener, View.OnClickListener {
    TextView nicknameText, passwordText, groupText; // поля в которых отображается информация юзера
    SettingsPresenter settingsPresenter;
    SettingsDialogEditText settingsDialogEditText;
    ArrayList<View> layouts = new ArrayList<>();
    SmartMaterialSpinner<String> langSpinner;
    int check = 0;

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
        layouts.add(currentFragment.findViewById(R.id.activity_settings_ll_nickname));
        layouts.add(currentFragment.findViewById(R.id.activity_settings_ll_password));

        // Добавление гитхаб и телеграм профилей
        attachListeners(R.id.activity_settings_bt_dev1_github, R.id.activity_settings_bt_dev1_telegram, "https://github.com/falcofemoralis", "https://t.me/falcofemoralis");
        attachListeners(R.id.activity_settings_bt_dev2_github, R.id.activity_settings_bt_dev2_telegram, "https://github.com/Derlados", "https://t.me/Derlados");

        for (View layout : layouts)
            layout.setOnClickListener(this);

        (currentFragment.findViewById(R.id.activity_settings_ll_exit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit();
            }
        });

        setChangeSpinner();

        this.settingsPresenter = new SettingsPresenter(this);
        return currentFragment;
    }

    public void setChangeSpinner() {
        langSpinner = currentFragment.findViewById(R.id.fragment_settings_sp_sort);
        langSpinner.setItem(Arrays.asList(getResources().getStringArray(R.array.languages)));

        ArrayList<Pair<String, String>> languages = LocaleHelper.getLanguages(App.getApp().context());
        String lang = LocaleHelper.getLanguage(getContext());
        for (Pair<String, String> element : languages) {
            if (element.second.contains(lang)) {
                final int index = languages.indexOf(element);
                langSpinner.setSelection(index);
                langSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (++check > 1) {
                            LogsManager.getInstance().updateLogs(LogType.CHANGED_LANGUAGE);
                            settingsPresenter.changeLanguage(position);
                        } else{
                            // fix one more call onItemSelected
                            //2021-09-15 00:07:27.768 22728-22728/com.BSLCommunity.CSN_student D/lang_test: 2
                            //2021-09-15 00:07:27.769 22728-22728/com.BSLCommunity.CSN_student D/lang_test: changeLanguage
                            //2021-09-15 00:07:27.770 22728-22728/com.BSLCommunity.CSN_student D/lang_test: updateUI
                            //2021-09-15 00:07:28.046 22728-22728/com.BSLCommunity.CSN_student D/lang_test: 0 - why ???
                            //2021-09-15 00:07:28.086 22728-22728/com.BSLCommunity.CSN_student D/lang_test: 2
                            if(position == 0 && index == 2 || position == 0 && index == 1){
                                check=0;
                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                break;
            }
        }


    }

    /**
     * Обработчик нажатия на диалоговое поле
     *
     * @param v - вью
     */
    @Override
    public void onClick(View v) {
        settingsDialogEditText.setApplyKey(v.getId(), this);
        settingsDialogEditText.show(getActivity().getSupportFragmentManager(), "DialogText");
    }

    /**
     * Кнопка выхода из аккаунта
     */
    public void exit() {
        showLogOutDialog();
    }

    /**
     * @see SettingsDialogEditText.DialogListener
     */
    @Override
    public void applyText(String text, int applyKey) {
        if (text != null) {
            if (applyKey == R.id.activity_settings_ll_nickname) {
                LogsManager.getInstance().updateLogs(LogType.CHANGED_NICKNAME);
                this.settingsPresenter.addNewValue(SettingsPresenter.DataKey.NickName, text);
            } else if (applyKey == R.id.activity_settings_ll_password) {
                LogsManager.getInstance().updateLogs(LogType.CHANGED_PASSWORD);
                this.settingsPresenter.addNewValue(SettingsPresenter.DataKey.Password, text);
            } else if (applyKey == R.id.activity_settings_ll_group) {
                // TODO смена группы пользователя
            }

            settingsPresenter.updateData();
        }
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
                OnFragmentInteractionListener.Action.NEXT_FRAGMENT_NO_BACK_STACK, null, null);
    }

    /**
     * @see SettingsView
     */
    @Override
    public void setDataToSettings(String nickName, String password, String group) {
        ArrayList<Pair<String, String>> languages = LocaleHelper.getLanguages(getContext());
        nicknameText.setText(nickName);
        passwordText.setText(password);
        groupText.setText(group);

        this.settingsDialogEditText = new SettingsDialogEditText(nickName, password);
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

    @Override
    public void setGuestMode() {
        currentFragment.findViewById(R.id.activity_settings_ll_nickname).setVisibility(View.GONE);
        currentFragment.findViewById(R.id.activity_settings_ll_password).setVisibility(View.GONE);
    }

    /**
     * @see SettingsView
     */
    @Override
    public void reloadActivity() {
        this.startActivity(new Intent(getContext(), MainActivity.class));
        getActivity().finishAffinity();
    }

    @Override
    public void updateUI() {
        Log.d("lang_test", "updateUI");

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
            Toast.makeText(getContext(), R.string.reload_hint, Toast.LENGTH_SHORT).show();
        } else {
            requireActivity().recreate();
        }
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
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    /**
     * Привязка обработчиков нажатий на кнопки гитхаб и телеграм профилей
     *
     * @param github      - id кнопки гитхаба
     * @param telegram    - id кнопки телеграма
     * @param githubURL   - ссылка на гитхаб профиль
     * @param telegramURL - ссылка на телеграм профиль
     */
    private void attachListeners(int github, int telegram, final String githubURL, final String telegramURL) {
        Button githubBtn = currentFragment.findViewById(github);
        Button telegramBtn = currentFragment.findViewById(telegram);

        githubBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogsManager.getInstance().updateLogs(LogType.OPENED_GITHUB, githubURL);
                Intent linkIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(githubURL));
                startActivity(linkIntent);
            }
        });

        telegramBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogsManager.getInstance().updateLogs(LogType.OPENED_TELEGRAM, telegramURL);
                Intent linkIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(telegramURL));
                startActivity(linkIntent);
            }
        });
    }
}
