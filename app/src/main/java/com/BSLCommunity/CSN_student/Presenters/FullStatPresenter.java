package com.BSLCommunity.CSN_student.Presenters;

import com.BSLCommunity.CSN_student.Constants.WorkType;
import com.BSLCommunity.CSN_student.Models.EditableSubject;
import com.BSLCommunity.CSN_student.Models.UserData;
import com.BSLCommunity.CSN_student.ViewInterfaces.FullStatView;

import java.util.ArrayList;

public class FullStatPresenter {

    FullStatView fullStatView;
    UserData userData;

    public FullStatPresenter(FullStatView fullStatView) {
        this.fullStatView = fullStatView;
        this.userData = UserData.getUserData();
        this.buildInterface();
    }

    /**
     * Построение интерфейса (передача во View всех данных для вывода таблицы на экран)
     */
    private void buildInterface() {
        int[] maxWorks = getMaxWorks();
        this.fullStatView.addWorksHeaders(maxWorks);

        for (int i = 0; i < this.userData.editableSubjects.size(); ++i) {
            EditableSubject edSubject = this.userData.editableSubjects.get(i);

            int idSubjectValue = edSubject.subjectValue.ordinal();
            this.fullStatView.addSubjectRow(edSubject.name, idSubjectValue, edSubject.allWorks, maxWorks);
        }
    }

    /**
     * Подсчет максимального количнства работ для каждого типа работы среди всех работ
     * @return массив максимального количества (лаб, ИДЗ и m.д.).
     *         Индекс массива соответствует номеру позации работ в enum WorkType
     */
    public int[] getMaxWorks() {
        final int countWorks = WorkType.values().length;
        int[] maxWorks = new int[countWorks];

        ArrayList<EditableSubject> edSubjects = this.userData.editableSubjects;
        for (int i = 0; i < edSubjects.size(); ++i) {
            EditableSubject edSubject = edSubjects.get(i);

            for (int j = 0; j < countWorks; ++j) {
                if (maxWorks[j] < edSubject.allWorks.get(WorkType.values()[j]).size()) {
                    maxWorks[j] = edSubject.allWorks.get(WorkType.values()[j]).size();
                }
            }
        }

        return maxWorks;
    }
}
