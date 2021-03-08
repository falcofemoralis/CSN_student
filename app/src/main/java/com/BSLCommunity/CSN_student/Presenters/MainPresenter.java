package com.BSLCommunity.CSN_student.Presenters;

import com.BSLCommunity.CSN_student.Models.UserData;
import com.BSLCommunity.CSN_student.ViewInterfaces.MainView;

public class MainPresenter {

    private final MainView mainView;
    private final UserData userData;

    public MainPresenter(MainView mainView) {
        this.mainView = mainView;
        this.userData = UserData.getUserData();
    }

    public void checkAuth() {
        if (this.userData.getToken() != null) {
            mainView.initActivity(this.userData.groupName, this.userData.course);
        } else {
            mainView.openLogin();
        }
    }
}
