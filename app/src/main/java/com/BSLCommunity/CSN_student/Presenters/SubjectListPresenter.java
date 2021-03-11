package com.BSLCommunity.CSN_student.Presenters;

import com.BSLCommunity.CSN_student.Models.Subject;
import com.BSLCommunity.CSN_student.Models.SubjectModel;
import com.BSLCommunity.CSN_student.ViewInterfaces.SubjectListView;
import com.BSLCommunity.CSN_student.lib.ExCallable;

import java.util.ArrayList;

public class SubjectListPresenter {

    private final SubjectListView subjectListView;
    private final SubjectModel subjectModel;

    public SubjectListPresenter(SubjectListView subjectListView) {
        this.subjectListView = subjectListView;
        this.subjectModel = SubjectModel.getSubjectModel();
        this.subjectModel.getGroupSubjects(6, new ExCallable<ArrayList<Subject>>() {
            @Override
            public void call(ArrayList<Subject> data) {

            }

            @Override
            public void fail(int idResString) {

            }
        });
    }
}
