package com.BSLCommunity.CSN_student.Presenters;

import com.BSLCommunity.CSN_student.Models.AppData;
import com.BSLCommunity.CSN_student.Models.Subject;
import com.BSLCommunity.CSN_student.Models.SubjectModel;
import com.BSLCommunity.CSN_student.ViewInterfaces.SubjectListView;
import com.BSLCommunity.CSN_student.lib.ExCallable;

import java.util.ArrayList;

public class SubjectListPresenter {

    private final SubjectListView subjectListView;
    private final SubjectModel subjectModel;
    private final AppData appData;

    public SubjectListPresenter(final SubjectListView subjectListView) {
        this.subjectListView = subjectListView;
        this.subjectModel = SubjectModel.getSubjectModel();
        this.appData = AppData.getAppData();

        initData();
    }

    /**
     * Инициализация данныз для отображение на эекране
     */
    private void initData() {
        this.subjectModel.getGroupSubjects(this.appData.userData.getGroupId(), new ExCallable<ArrayList<Subject>>() {
            @Override
            public void call(ArrayList<Subject> data) {
                appData.createEditableSubjects(data);
                subjectListView.setTableSubjects(appData.editableSubjects);
            }

            @Override
            public void fail(int idResString) {

            }
        });
        this.subjectListView.setCourse(this.appData.userData.getCourse());
    }

    /**
     * Пользователь возвращается на окно дисциплин (обновление прогресса на каждом из предметов)
     */
    public void resume() {
        this.subjectListView.updateProgresses(appData.editableSubjects);
    }
}
