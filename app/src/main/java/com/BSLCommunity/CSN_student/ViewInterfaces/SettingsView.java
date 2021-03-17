package com.BSLCommunity.CSN_student.ViewInterfaces;

import android.util.Pair;

import java.util.ArrayList;

public interface SettingsView {
    /**
     * Демонстрация ошибки в тосте
     *
     * @param id - id текста из ресурсов
     */
    void showToast(int id);

    /**
     * Открытие окна логина после разлогинивания пользователя
     */
    void openLogin();

    /**
     * Установка первоначальных данных для отображения в меню настроек
     *
     * @param nickName  - никнейм
     * @param password  - пароль
     * @param group     - группа
     */
    void setDataToSettings(String nickName, String password, String group);

    /**
     * Перезагрузка активити
     */
    void reloadActivity();

    /**
     * Обновление данных во View
     *
     * @param nickName - никнейм
     * @param password - password
     */
    void updateData(String nickName, String password);
}
