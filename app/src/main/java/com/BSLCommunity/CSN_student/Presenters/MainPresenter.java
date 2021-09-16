package com.BSLCommunity.CSN_student.Presenters;

import com.BSLCommunity.CSN_student.Constants.CacheStatusType;
import com.BSLCommunity.CSN_student.Constants.ProgressType;
import com.BSLCommunity.CSN_student.Models.DataModel;
import com.BSLCommunity.CSN_student.Models.UserData;
import com.BSLCommunity.CSN_student.ViewInterfaces.MainView;
import com.BSLCommunity.CSN_student.lib.ExCallable;

public class MainPresenter {

    private final MainView mainView;
    private final UserData userData;
    private final DataModel dataModel;

    public MainPresenter(MainView mainView) {
        this.mainView = mainView;
        this.userData = UserData.getUserData();
        this.dataModel = DataModel.getDataModel();
    }

    /**
     * Проверка авторизации пользователя. Также идет проверка кеш файла, в случае его обновления, данные будут перекачены.
     */
    public void checkAuth() {
        if (!this.userData.isGuest && this.userData.user.getToken() == null) {
            mainView.openLogin();
            return;
        }

        if (this.userData.isGuest) {
            mainView.setGuestMode();
        }
        mainView.initFragment(this.userData.user.getGroupName(), this.userData.user.getCourse());
        userData.setUserOpens();

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
    }

    /**
     * Инциализция загрузки. Устанавливается диалоговое окно. Идет подсчет данных которые должны будут скачаны
     */
    public void initDownload() {
        dataModel.downloadCache(new ExCallable<ProgressType>() {
            @Override
            public void call(ProgressType data) {
                mainView.showProgressDialog(dataModel.dataToDownload.size());
                tryDownload();
            }

            @Override
            public void fail(int idResString) {
                //Ошибка скачивания кеша, никак не отслеживаем
            }
        });
    }

    /**
     * Скачивание данных с сервера
     */
    public void tryDownload() {
        mainView.showProgressDialog(-1);

        dataModel.downloadData(UserData.getUserData().isGuest, new ExCallable<ProgressType>() {
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
