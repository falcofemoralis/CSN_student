package com.BSLCommunity.CSN_student.Presenters;

import com.BSLCommunity.CSN_student.Models.SubjectModel;
import com.BSLCommunity.CSN_student.Models.UserData;
import com.BSLCommunity.CSN_student.ViewInterfaces.SubjectListView;

public class SubjectListPresenter {

    private final SubjectListView subjectListView;
    private final SubjectModel subjectModel;
    private final UserData userData;

    public SubjectListPresenter(final SubjectListView subjectListView) {
        this.subjectListView = subjectListView;
        this.subjectModel = SubjectModel.getSubjectModel();
        this.userData = UserData.getUserData();

        initData();
    }

    /**
     * Инициализация данных для отображение на эекране
     */
    private void initData() {
        userData.createEditableSubjects(this.subjectModel.subjects);
        subjectListView.setTableSubjects(userData.editableSubjects);
        this.subjectListView.setCourse(this.userData.user.getCourse());
    }

    /**
     * Пользователь возвращается на окно дисциплин (обновление прогресса на каждом из предметов)
     */
    public void resume() {
        this.subjectListView.updateProgresses(userData.editableSubjects);
    }
}
