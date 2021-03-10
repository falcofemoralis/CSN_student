package com.BSLCommunity.CSN_student.Presenters;

import com.BSLCommunity.CSN_student.Models.AppData;
import com.BSLCommunity.CSN_student.ViewInterfaces.MainView;

public class MainPresenter {

    private final MainView mainView;
    private final AppData appData;

    public MainPresenter(MainView mainView) {
        this.mainView = mainView;
        this.appData = AppData.getAppData();
    }

    public void checkAuth() {
        if (this.appData.userData.getToken() != null) {
            mainView.initActivity(this.appData.userData.getGroupName(), this.appData.userData.getCourse());
        } else {
            mainView.openLogin();
        }
    }
}
