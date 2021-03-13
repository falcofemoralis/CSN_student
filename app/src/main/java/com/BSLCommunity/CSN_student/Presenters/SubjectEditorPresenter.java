package com.BSLCommunity.CSN_student.Presenters;

import com.BSLCommunity.CSN_student.Constants.SubjectValue;
import com.BSLCommunity.CSN_student.Constants.WorkStatus;
import com.BSLCommunity.CSN_student.Constants.WorkType;
import com.BSLCommunity.CSN_student.Models.AppData;
import com.BSLCommunity.CSN_student.Models.EditableSubject;
import com.BSLCommunity.CSN_student.ViewInterfaces.SubjectEditorView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class SubjectEditorPresenter {

    SubjectEditorView subjectEditorView;
    AppData appData;
    EditableSubject copyEdSubject;

    public SubjectEditorPresenter(SubjectEditorView subjectEditorView, String intentData) {
        this.subjectEditorView = subjectEditorView;
        this.appData = AppData.getAppData();

        Type type = new TypeToken<EditableSubject>(){}.getType();
        this.copyEdSubject = (new Gson()).fromJson(intentData, type);

        this.subjectEditorView.setSubjectData(this.copyEdSubject);
    }

    public void addWork(WorkType workType) {
        this.copyEdSubject.allWorks.get(workType).add(new EditableSubject.Work());
    }

    public void deleteWork(WorkType workType, int index) {
        this.copyEdSubject.allWorks.get(workType).remove(index);
    }

    public void changeWork(WorkType workType, int index, String name, WorkStatus workStatus, String mark) {
        EditableSubject.Work editableWork = this.copyEdSubject.allWorks.get(workType).get(index);
        editableWork.name = name;
        editableWork.workStatus = workStatus;
        editableWork.mark = mark;

        this.subjectEditorView.setWorkProgress(this.copyEdSubject.calculateProgress());
    }

    public void changeSubjectValue(SubjectValue subjectValue) {
        this.copyEdSubject.subjectValue = subjectValue;
    }

    public void finishEdit() {
        int index = -1;
        for (int i = 0; i < this.appData.editableSubjects.size(); ++i) {
            if (this.appData.editableSubjects.get(i).name.equals(this.copyEdSubject.name)) {
                index = i;
                break;
            }
        }

        this.appData.editableSubjects.get(index).allWorks = this.copyEdSubject.allWorks;
        this.appData.editableSubjects.get(index).subjectValue = this.copyEdSubject.subjectValue;
        this.appData.saveData();
    }
}
