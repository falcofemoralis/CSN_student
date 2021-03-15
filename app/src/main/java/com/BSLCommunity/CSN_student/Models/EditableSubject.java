package com.BSLCommunity.CSN_student.Models;

import com.BSLCommunity.CSN_student.Constants.SubjectValue;
import com.BSLCommunity.CSN_student.Constants.WorkStatus;
import com.BSLCommunity.CSN_student.Constants.WorkType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditableSubject extends Subject {
    public static class Work {
        public String name; // Название каждой работы
        public String mark; // Оценка за каждую работу
        public WorkStatus workStatus;

        public Work() {
            this.name = "";
            this.mark = "";
            this.workStatus = WorkStatus.NOT_PASSED;
        }
    }

    public HashMap<WorkType, ArrayList<Work>> allWorks;
    public SubjectValue subjectValue;

    public EditableSubject(Subject subject) {
        super(subject.idTeachers, subject.name, subject.imgPath, subject.img);

        allWorks = new HashMap<>();
        allWorks.put(WorkType.LABS, new ArrayList<Work>());
        allWorks.put(WorkType.IHW, new ArrayList<Work>());
        allWorks.put(WorkType.OTHERS, new ArrayList<Work>());

        this.subjectValue = SubjectValue.EXAM;
    }

    public void setSubjectValue(SubjectValue subjectValue) {
        this.subjectValue = subjectValue;
    }

    public void setAllWorks(HashMap<WorkType, ArrayList<Work>> allWorks) {
        this.allWorks = allWorks;
    }

    /**
     * Подсчет прогресса по все дисциплине
     * @return - прогресс в процентах
     */
    public int calculateProgress() {
        int subjectComplete = 0;
        int sumCount = 0;

        for (Map.Entry<WorkType, ArrayList<Work>> work : allWorks.entrySet()) {
            ArrayList<Work> typeWorks = work.getValue();

            for (int i = 0; i < typeWorks.size(); ++i)
                if (typeWorks.get(i).workStatus == WorkStatus.PASSED_WITH_REPORT) {
                    subjectComplete++;
                    sumCount = work.getValue().size();
                }
        }

        return sumCount > 0 ? 100 * subjectComplete / sumCount : 0;
    }
}