package com.BSLCommunity.CSN_student.ViewInterfaces;

public interface LoginView {
    /**
     * Демонстрация ошибки в тосте
     * @param id - id текста из ресурсов
     */
    void showToastError(int id);
    /**
     * Открытие главного окна приложения после удачной регистрации
     */
    void openMain();
}
