package com.BSLCommunity.CSN_student.ViewInterfaces;

import com.BSLCommunity.CSN_student.Constants.ProgressType;

public interface MainView {

    /**
     * Инициализация активити
     * @param groupName - имя группы
     * @param course - номер курса
     */
    void initFragment(String groupName, int course);
    /**
     * Открытие окна логина
     */
    void openLogin();
    /**
     * Открытие окна загрузки файла
     */
    void showProgressDialog(int size);
    /**
     * Управление диалогом загрузки
     */
    void controlProgressDialog(ProgressType type, boolean isFirst);
}
