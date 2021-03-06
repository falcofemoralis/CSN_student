package com.BSLCommunity.CSN_student.Presenters;

import com.BSLCommunity.CSN_student.Models.GroupModel;
import com.BSLCommunity.CSN_student.R;
import com.BSLCommunity.CSN_student.ViewInterfaces.RegView;
import com.BSLCommunity.CSN_student.lib.CallBack;

import java.util.ArrayList;
import java.util.Collections;

public class RegPresenter {

    private final String validRegEx = "([A-z]|[А-я]|і|[0-9])+"; // Регулярка для проверки валидации
    private final RegView regView; // View регистрации
    private final GroupModel groupModel; // Модель групп, нужна для получения информации о группах для выбора при регистрации

    public RegPresenter(RegView regView) {
        this.regView = regView;
        this.groupModel = GroupModel.getGroupModel();
    }

    /**
     * Попытка регистрации нового пользователя. Проверка валидации и при успешной валидации - регистрация
     * @param nickname - никнейм
     * @param password - пароль
     * @param confPassword - подтверджение пароля
     */
    public void tryRegistration(String nickname, String password, String confPassword) {
        if (!nickname.matches(validRegEx) || !nickname.matches(validRegEx) || !nickname.matches(validRegEx)) {
            this.regView.showToastError(R.string.invalid_data);
        }
        else if (!password.equals(confPassword))  {
            this.regView.showToastError(R.string.passwords_do_not_match);
        }
        else {
            this.regView.showToastError(R.string.no_connection_server);
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
            public void fail(String message) {
                try {
                    regView.showToastError(R.string.no_connection_server);
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
