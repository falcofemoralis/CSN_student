package com.BSLCommunity.CSN_student.ViewInterfaces;

import com.BSLCommunity.CSN_student.Constants.GrantType;
import com.BSLCommunity.CSN_student.Constants.MarkErrorType;
import com.BSLCommunity.CSN_student.Constants.SubjectValue;

public interface GradeCalculatorView {

    /**
     * Установка дисциплины
     *
     * @param name - имя дисциплины
     * @param type - вес дисциплины
     */
    void setSubject(String name, SubjectValue type);

    /**
     * Установка результата
     *
     * @param result100 - по 100 бальной системе
     * @param result5   - по 5 бальной
     */
    void showResult(float result100, float result5);

    /**
     * Показ сообщений
     * @param type - тип сообщений
     */
    void showMsg(MarkErrorType type);

    /**
     * Установка стипендии
     * @param type - вид стипендии
     */
    void setGrant(GrantType type);

    /**
     * Установка отображение предметов
     */
    void showSubjects();
}
