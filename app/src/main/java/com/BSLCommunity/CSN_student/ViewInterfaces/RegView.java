package com.BSLCommunity.CSN_student.ViewInterfaces;

import java.util.ArrayList;

public interface RegView {

    /**
     * Демонстрация ошибки в тосте
     *
     * @param error - текст
     */
    void showToastError(String error);

    /**
     * Открытие главного окна приложения после удачной регистрации
     */
    void openMain();

    /**
     * Установка данных в спинеры
     *
     * @param groupNames - имена групп (по умолчанию берется первый курс)
     * @param courses    - курсы
     */
    void setSpinnersData(ArrayList<String> groupNames, ArrayList<String> courses);

    /**
     * Установка имен групп в спиннер для групп
     *
     * @param groupNames - имена групп
     */
    void setGroupNamesSpinner(ArrayList<String> groupNames);

    /**
     * Отображение прогресс бара при загрузке в спиннер данных
     *
     * @param show - true - отображение, false - скрытие
     */
    void visibilityProgressBar(boolean show);
}
