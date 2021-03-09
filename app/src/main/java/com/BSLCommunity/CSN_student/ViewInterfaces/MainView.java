package com.BSLCommunity.CSN_student.ViewInterfaces;

public interface MainView {

    /**
     * Инициализация активити
     * @param groupName - имя группы
     * @param course - номер курса
     */
    void initActivity(String groupName, int course);
    /**
     * Открытие окна логина
     */
    void openLogin();
}
