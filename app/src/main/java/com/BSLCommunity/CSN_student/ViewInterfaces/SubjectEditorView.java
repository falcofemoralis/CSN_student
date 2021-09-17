package com.BSLCommunity.CSN_student.ViewInterfaces;

import com.BSLCommunity.CSN_student.Constants.WorkType;
import com.BSLCommunity.CSN_student.Models.Entity.EditableSubject;

public interface SubjectEditorView {
    void setSubjectData(EditableSubject editableSubject);
    void setWorkProgress(int progress);
    void deleteWorkRow(WorkType workType, int index);
}
