package com.BSLCommunity.CSN_student.Presenters;

import com.BSLCommunity.CSN_student.Models.GroupModel;
import com.BSLCommunity.CSN_student.Models.UserData;
import com.BSLCommunity.CSN_student.Models.UserModel;
import com.BSLCommunity.CSN_student.R;
import com.BSLCommunity.CSN_student.ViewInterfaces.RegView;
import com.BSLCommunity.CSN_student.lib.CallBack;

import java.util.ArrayList;
import java.util.Collections;

public class RegPresenter {

    private final String validRegEx = "([A-Z,a-z]|[А-Я,а-я]|[ІЇЄiїєЁё]|[0-9])+"; // Регулярка для проверки валидации
    private final RegView regView; // View регистрации
    private final GroupModel groupModel; // Модель групп, нужна для получения информации о группах для выбора при регистрации
    private final UserModel userModel; // Модель пользователя, нужна для регистрации
    private final UserData userData;

    public RegPresenter(RegView regView) {
        this.regView = regView;
        this.groupModel = GroupModel.getGroupModel();
        this.userModel= UserModel.getUserModel();
        this.userData = UserData.getUserData();
    }

    /**
     * Попытка регистрации нового пользователя. Проверка валидации и при успешной валидации - регистрация
     * @param nickname - никнейм
     * @param password - пароль
     * @param confPassword - подтверджение пароля
     */
    public void tryRegistration(String nickname, String password, String confPassword, String groupName) {
        if (!nickname.matches(validRegEx) || !password.matches(validRegEx) || !confPassword.matches(validRegEx)) {
            this.regView.showToastError(R.string.invalid_data);
        }
        else if (!password.equals(confPassword))  {
            this.regView.showToastError(R.string.passwords_do_not_match);
        }
        else {
            this.userModel.registration(nickname, password, groupName, new CallBack<UserData>() {
                @Override
                public void call(UserData data) {
                    try {
                        userData.updateUserData(data);
                        regView.openMain();
                    } catch (Exception ignored) {}
                }

                @Override
                public void fail(int idResString) {
                    try {
                        regView.showToastError(idResString);
                    } catch (Exception ignored){}
                }
            });
        }
    }

    /**
     * Загрузка данных необходимых для спиннеров
     */
    public void initSpinnerData() {
        this.regView.visibilityProgressBar(true);

        this.groupModel.getAllGroups(new CallBack<ArrayList<GroupModel.Group>>() {
            @Override
            public void call(ArrayList<GroupModel.Group> response) {
                final int defaultCourse = 1;

                ArrayList<String> groupNames = new ArrayList<>();
                ArrayList<String> courses = new ArrayList<>();

                for (int i = 0; i < response.size(); ++i) {
                    GroupModel.Group group = response.get(i);
                    groupNames.add(group.groupName);
                    if (!courses.contains(Integer.toString(group.course))) {
                        courses.add(Integer.toString(group.course));
                    }
                }
                Collections.sort(courses);

                try {
                    regView.setSpinnersData(groupModel.getGroupsOnCourse(defaultCourse), courses);
                    regView.visibilityProgressBar(false);
                } catch (Exception ignored) {}
            }

            @Override
            public void fail(int idResString) {
                try {
                    regView.showToastError(idResString);
                } catch (Exception ignored) {}
            }
        });
    }

    /**
     * Загрузка групп опредленного курса
     * @param course - номер курса
     */
    public void chosenCourse(int course) {
        ArrayList<String> groupsName = this.groupModel.getGroupsOnCourse(course);
        this.regView.setGroupNamesSpinner(groupsName);
    }

}
