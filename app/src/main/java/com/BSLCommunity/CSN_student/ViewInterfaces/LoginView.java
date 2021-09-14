package com.BSLCommunity.CSN_student.ViewInterfaces;

public interface LoginView {
    /**
     * Демонстрация ошибки в тосте
     * @param error - текст
     */
    void showToastError(String error);
    /**
     * Открытие главного окна приложения после удачной регистрации
     */
    void openMain();
}
