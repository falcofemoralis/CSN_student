package com.BSLCommunity.CSN_student.Presenters;

import android.content.Context;

import com.BSLCommunity.CSN_student.Constants.CacheStatusType;
import com.BSLCommunity.CSN_student.Constants.ProgressType;
import com.BSLCommunity.CSN_student.Managers.FileManager;
import com.BSLCommunity.CSN_student.Models.DataModel;
import com.BSLCommunity.CSN_student.Models.UserData;
import com.BSLCommunity.CSN_student.ViewInterfaces.MainView;
import com.BSLCommunity.CSN_student.lib.ExCallable;

public class MainPresenter {

    private final MainView mainView;
    private final UserData userData;
    private final DataModel dataModel;

    public MainPresenter(MainView mainView, Context context) {
        FileManager.init(context);
        this.mainView = mainView;
        this.userData = UserData.getUserData();
        this.dataModel = DataModel.getDataModel(context);
    }

    public void checkAuth() {
        if (this.userData.user.getToken() != null) {
            mainView.initActivity(this.userData.user.getGroupName(), this.userData.user.getCourse());
            dataModel.checkCache(new ExCallable<CacheStatusType>() {
                @Override
                public void call(CacheStatusType cacheStatus) {
                    if (cacheStatus == CacheStatusType.NO_CACHE || cacheStatus == CacheStatusType.CACHE_NEED_UPDATE) {
                        initDownload();
                    }
                }

                @Override
                public void fail(int idResString) {
                }
            });
        } else {
            mainView.openLogin();
        }
    }

    public void initDownload() {
        mainView.showProgressDialog(dataModel.initDataToDownload());

        dataModel.downloadCache(new ExCallable<ProgressType>() {
            @Override
            public void call(ProgressType data) {
                tryDownload();
            }

            @Override
            public void fail(int idResString) {
                mainView.controlProgressDialog(ProgressType.SET_FAIL, dataModel.clientCache == null);
            }
        });
    }

    // Попытка скачать данные
    public void tryDownload() {
        DataModel.isFailed = false;
        mainView.showProgressDialog(-1);

        dataModel.downloadData(new ExCallable<ProgressType>() {
            @Override
            public void call(ProgressType data) {
                if (data == ProgressType.SET_OK) dataModel.save();
                mainView.controlProgressDialog(data, true);
            }

            @Override
            public void fail(int idResString) {
                mainView.controlProgressDialog(ProgressType.SET_FAIL, dataModel.clientCache == null);
            }
        });
    }
}
