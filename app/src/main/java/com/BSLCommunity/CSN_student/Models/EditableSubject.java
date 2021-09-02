package com.BSLCommunity.CSN_student.Models;

import com.BSLCommunity.CSN_student.Constants.SubjectValue;
import com.BSLCommunity.CSN_student.Constants.WorkStatus;
import com.BSLCommunity.CSN_student.Constants.WorkType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditableSubject {
    public static class Work {
        public String name; // Название каждой работы
        public String mark; // Оценка за каждую работу
        public WorkStatus workStatus;
        public boolean isExam;

        public Work() {
            this.name = "";
            this.mark = "";
            this.workStatus = WorkStatus.NOT_PASSED;
            this.isExam = false;
        }
    }

    public int idSubject;
    public HashMap<WorkType, ArrayList<Work>> allWorks;
    public SubjectValue subjectValue;

    public EditableSubject(int idSubject) {
        //super(subject.idTeachers, subject.name, subject.imgPath);
        this.idSubject = idSubject;

        allWorks = new HashMap<>();
        allWorks.put(WorkType.LABS, new ArrayList<Work>());
        allWorks.put(WorkType.IHW, new ArrayList<Work>());
        allWorks.put(WorkType.OTHERS, new ArrayList<Work>());

        this.subjectValue = SubjectValue.TEST;
    }

    public void setSubjectValue(SubjectValue subjectValue) {
        this.subjectValue = subjectValue;
    }

    public void setAllWorks(HashMap<WorkType, ArrayList<Work>> allWorks) {
        this.allWorks = allWorks;
    }

    /**
     * Подсчет прогресса по все дисциплине
     *
     * @return - прогресс в процентах
     */
    public int calculateProgress() {
        int subjectComplete = 0;
        int sumCount = 0;

        for (Map.Entry<WorkType, ArrayList<Work>> works : allWorks.entrySet()) {
            ArrayList<Work> oneTypeWorks = works.getValue();
            sumCount += works.getValue().size();

            for (int i = 0; i < oneTypeWorks.size(); ++i)
                if (oneTypeWorks.get(i).workStatus == WorkStatus.PASSED_WITH_REPORT) {
                    subjectComplete++;
                }
        }

        return sumCount > 0 ? 100 * subjectComplete / sumCount : 0;
    }
}