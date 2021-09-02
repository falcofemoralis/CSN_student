package com.BSLCommunity.CSN_student.ViewInterfaces;

import com.BSLCommunity.CSN_student.Models.EditableSubject;

import java.util.ArrayList;

public interface SubjectListView {
    void setTableSubjects(ArrayList<EditableSubject> subjects);
    void setCourse(int course);
    void updateProgresses(int[] subjectProgresses, int sumProgress);
}
