package com.BSLCommunity.CSN_student.ViewInterfaces;

import com.BSLCommunity.CSN_student.Models.EditableSubject;

import java.util.ArrayList;

public interface SubjectListView {

    /**
     * Отрисовка всей таблицы дисциплин
     * @param subjects - дисциплины с информацией пользователя о прогрессе
     */
    void setTableSubjects(ArrayList<EditableSubject> subjects);
    /**
     * Установка курса пользователя
     * @param course - номер курса
     */
    void setCourse(int course);
    /**
     * Обновление прогресса на кнопках дисциплин
     * @param subjectProgresses - прогрессы по дисциплинам
     * @param sumProgress - суммарный прогресс
     */
    void updateProgresses(int[] subjectProgresses, int sumProgress);
}
