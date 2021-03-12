package com.BSLCommunity.CSN_student.Models;

import java.util.ArrayList;

public class EditableSubject extends Subject {
    public static class Work {
        public int count; // Количество работ
        public ArrayList<Integer> values; // Статус выполнения каждой работы
        public ArrayList<String> names; // Название каждой работы
        public ArrayList<Integer> marks; // Оценка за каждую работу

        public Work() {
            values = new ArrayList<Integer>();
            names = new ArrayList<String>();
            marks = new ArrayList<Integer>();
        }

        // Добавление пустого объекта
        public void addWork() {
            ++count;
            values.add(0);
            names.add("");
            marks.add(0);
        }

        // Удаление элемента по индексу
        public void deleteWork(int index) {
            --count;
            values.remove(index);
            names.remove(index);
            marks.remove(index);
        }
    }
    public EditableSubject.Work labs, ihw, others;

    public EditableSubject(Subject subject) {
        super(subject.idTeachers, subject.name, subject.imgPath);
        labs = new EditableSubject.Work();
        ihw = new EditableSubject.Work();
        others = new EditableSubject.Work();
    }

    public int calculateProgress() {
        final int COMPLETE = 6;

        int subjectComplete = 0;

        // Считаем сколько он выполнил лабораторных, ИДЗ, других дел
        for (int i = 0; i < labs.count; ++i)
            if (labs.values.get(i) == COMPLETE) subjectComplete++;

        for (int i = 0; i < ihw.count; ++i)
            if (ihw.values.get(i) == COMPLETE) subjectComplete++;

        for (int i = 0; i < others.count; ++i)
            if (others.values.get(i) == COMPLETE) subjectComplete++;

        int sumCount = (labs.count + ihw.count + others.count);
        return sumCount > 0 ? 100 * subjectComplete / sumCount : 0;
    }
}