package com.BSLCommunity.CSN_student.ViewInterfaces;

import com.BSLCommunity.CSN_student.Models.Subject;

import java.util.ArrayList;

public interface SubjectListView {

    /**
     * Отрисовка всей таблицы дисциплин
     * @param subjects - дисциплины
     */
    void setTableSubjects(ArrayList<Subject> subjects);
    /**
     * Установка курса пользователя
     * @param course - номер курса
     */
    void setCourse(int course);
}
