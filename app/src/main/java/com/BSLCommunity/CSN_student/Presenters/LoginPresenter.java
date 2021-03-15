package com.BSLCommunity.CSN_student.Presenters;

import com.BSLCommunity.CSN_student.Models.User;
import com.BSLCommunity.CSN_student.Models.UserData;
import com.BSLCommunity.CSN_student.Models.UserModel;
import com.BSLCommunity.CSN_student.R;
import com.BSLCommunity.CSN_student.ViewInterfaces.LoginView;
import com.BSLCommunity.CSN_student.lib.ExCallable;

public class LoginPresenter {

    private final String validRegEx = "([A-Z,a-z]|[А-Я,а-я]|[ІЇЄiїєЁё]|[0-9])+"; // Регулярка для проверки валидации
    private final LoginView loginView; // View регистрации
    private final UserModel userModel; // Модель пользователя, нужна для логина
    private final UserData userData;

    public LoginPresenter(LoginView loginView) {
        this.loginView = loginView;
        this.userModel = UserModel.getUserModel();
        this.userData = UserData.getUserData();
    }

    /**
     * Попытка авторизации пользователя
     * @param nickname - никнейм
     * @param password - пароль
     */
    public void tryLogin(String nickname, String password) {
        if (!nickname.matches(validRegEx) || !password.matches(validRegEx)) {
            this.loginView.showToastError(R.string.invalid_data);
        }
        else {
            this.userModel.login(nickname, password, new ExCallable<User>() {
                @Override
                public void call(User data) {
                    try {
                        userData.setUser(data);
                        loginView.openMain();
                    } catch (Exception ignored) {}
                }

                @Override
                public void fail(int idResString) {
                    try {
                        loginView.showToastError(idResString);
                    } catch (Exception ignored){}
                }
            });
        }
    }
}
