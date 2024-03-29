package com.BSLCommunity.CSN_student.Presenters;

import com.BSLCommunity.CSN_student.Constants.SubjectValue;
import com.BSLCommunity.CSN_student.Constants.WorkStatus;
import com.BSLCommunity.CSN_student.Constants.WorkType;
import com.BSLCommunity.CSN_student.Models.Entity.EditableSubject;
import com.BSLCommunity.CSN_student.Models.SubjectModel;
import com.BSLCommunity.CSN_student.Models.UserData;
import com.BSLCommunity.CSN_student.ViewInterfaces.SubjectEditorView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class SubjectEditorPresenter {

    SubjectEditorView subjectEditorView;
    UserData userData;
    EditableSubject copyEdSubject;

    public SubjectEditorPresenter(SubjectEditorView subjectEditorView, String intentData) {
        this.subjectEditorView = subjectEditorView;
        this.userData = UserData.getUserData();

        Type type = new TypeToken<EditableSubject>(){}.getType();
        this.copyEdSubject = (new Gson()).fromJson(intentData, type);

        this.subjectEditorView.setSubjectData(this.copyEdSubject);
    }

    public void addWork(WorkType workType) {
        this.copyEdSubject.allWorks.get(workType).add(new EditableSubject.Work());
        this.subjectEditorView.setWorkProgress(this.copyEdSubject.calculateProgress());
    }

    public void deleteWork(WorkType workType, int index) {
        try {
            this.copyEdSubject.allWorks.get(workType).remove(index);
            this.subjectEditorView.setWorkProgress(this.copyEdSubject.calculateProgress());
            this.subjectEditorView.deleteWorkRow(workType, index);
        } catch (Exception ignored) {}
    }

    public void changeWork(WorkType workType, int index, String name, WorkStatus workStatus, String mark, boolean isExamWork) {
        EditableSubject.Work editableWork = this.copyEdSubject.allWorks.get(workType).get(index);
        editableWork.name = name;
        editableWork.workStatus = workStatus;
        editableWork.mark = mark;
        if(workType == WorkType.OTHERS){
            editableWork.isExam = isExamWork;
        }

        this.subjectEditorView.setWorkProgress(this.copyEdSubject.calculateProgress());
    }

    public void changeSubjectValue(SubjectValue subjectValue) {
        this.copyEdSubject.subjectValue = subjectValue;
    }

    public void finishEdit() {
        int index = -1;
        for (int i = 0; i < this.userData.editableSubjects.size(); ++i) {
            SubjectModel subjectModel = SubjectModel.getSubjectModel();
            if (subjectModel.findById(this.userData.editableSubjects.get(i).idSubject).name.equals(subjectModel.findById(this.copyEdSubject.idSubject).name)) {
                index = i;
                break;
            }
        }

        this.userData.editableSubjects.get(index).allWorks = this.copyEdSubject.allWorks;
        this.userData.editableSubjects.get(index).subjectValue = this.copyEdSubject.subjectValue;
        this.userData.saveData();
    }
}
