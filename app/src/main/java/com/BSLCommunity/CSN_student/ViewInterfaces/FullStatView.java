package com.BSLCommunity.CSN_student.ViewInterfaces;

import com.BSLCommunity.CSN_student.Constants.WorkType;
import com.BSLCommunity.CSN_student.Models.Entity.EditableSubject;

import java.util.ArrayList;
import java.util.HashMap;

public interface FullStatView {
    /**
     * Отрисовка заголовко стобцов таблицы
     * @param maxWorks - максимальное количество работ для каждого типа (для корректной размерности таблицы)
     */
    void addWorksHeaders(int[] maxWorks);
    /**
     * Добавление строки дисциплины
     * @param subjectName - название дисциплины
     * @param idSubjectValue - индекс (номер) ценности объект. Порядковый номер в enum SubjectValue
     * @param allWorks - список всех рабор
     * @param maxWorks - максимальное количество работ для каждого типа (для корректной размерности таблицы)
     */
    void addSubjectRow(String subjectName, int idSubjectValue, HashMap<WorkType, ArrayList<EditableSubject.Work>> allWorks, int[] maxWorks);
}
