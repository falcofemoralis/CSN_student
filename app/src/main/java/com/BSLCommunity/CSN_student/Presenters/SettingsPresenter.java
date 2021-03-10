package com.BSLCommunity.CSN_student.Presenters;

import com.BSLCommunity.CSN_student.App;
import com.BSLCommunity.CSN_student.Managers.LocaleHelper;
import com.BSLCommunity.CSN_student.Models.AppData;
import com.BSLCommunity.CSN_student.Models.UserModel;
import com.BSLCommunity.CSN_student.R;
import com.BSLCommunity.CSN_student.ViewInterfaces.SettingsView;
import com.BSLCommunity.CSN_student.lib.ExCallable;

import java.util.HashMap;

public class SettingsPresenter {

    public enum DataKey {
        NickName,
        Password
    }

    private final String validRegEx = "([A-Z,a-z]|[А-Я,а-я]|[ІЇЄiїєЁё]|[0-9])+"; // Регулярка для проверки валидации
    SettingsView settingsView; // View
    AppData appData; // Локальные данные
    UserModel userModel; // Модель пользователя, нужна для обновления данных в базе
    HashMap<String, String> dataToUpdate = new HashMap<>(); // Данные для обновления

    public SettingsPresenter(SettingsView settingsView) {
        this.settingsView = settingsView;
        this.appData = AppData.getAppData();
        this.userModel = UserModel.getUserModel();

        // Инициализация первоначальных данных (те которые установлены на данный момент)
        dataToUpdate.put(DataKey.NickName.toString(), this.appData.userData.getNickName());
        dataToUpdate.put(DataKey.Password.toString(), this.appData.userData.getPassword());

        settingsView.setDataToSettings(this.appData.userData.getNickName(), this.appData.userData.getPassword(),
                this.appData.userData.getGroupName(), this.appData.languages);
    }

    /**
     * Добавление изменений в аккаунте
     * @param key - атрибут
     * @param value - значение
     */
    public void addNewValue(DataKey key, String value) {
        if (!value.matches(validRegEx)) {
            this.settingsView.showToast(R.string.invalid_data);
        } else {
            this.dataToUpdate.put(key.toString(), value);
        }
    }

    /**
     * Выход из аккаунта и очистка данных
     */
    public void logOut() {
        this.appData.clearData();
        settingsView.openLogin();
    }

    /**
     * Обновление данных пользователя
     * При успешно обновлении обновляются локальные данные и отображение во View
     */
    public void updateData() {
        this.userModel.update(dataToUpdate.get(DataKey.NickName.toString()), dataToUpdate.get(DataKey.Password.toString()), this.appData.userData.getToken(), new ExCallable<Void>() {
            @Override
            public void call(Void data) {
                String nickName = dataToUpdate.get(DataKey.NickName.toString());
                String password = dataToUpdate.get(DataKey.Password.toString());

                appData.updateUserData(nickName, password);
                settingsView.updateData(nickName, password);
                settingsView.showToast(R.string.datachanged);
            }

            @Override
            public void fail(int idResString) {
                if (idResString != R.string.no_auth) {
                    settingsView.showToast(idResString);
                } else {
                    settingsView.showToast(idResString);
                    settingsView.openLogin();
                }
            }
        });
    }

    /**
     * Изменение языка в приложении
     * @param index - индекс в массиве языков
     */
    public void changeLanguage(int index) {
        LocaleHelper.setLocale(App.getApp().getApplicationContext(), this.appData.languages.get(index).second);
        this.settingsView.reloadActivity();
    }
}
