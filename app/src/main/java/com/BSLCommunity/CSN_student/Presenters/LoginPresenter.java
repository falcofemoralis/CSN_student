package com.BSLCommunity.CSN_student.Presenters;

import android.content.Context;

import com.BSLCommunity.CSN_student.Models.User;
import com.BSLCommunity.CSN_student.Models.UserData;
import com.BSLCommunity.CSN_student.Models.UserModel;
import com.BSLCommunity.CSN_student.R;
import com.BSLCommunity.CSN_student.ViewInterfaces.LoginView;
import com.BSLCommunity.CSN_student.lib.ExCallable;

public class LoginPresenter {

    private final String validRegEx = "([A-Z,a-z]|[А-Я,а-я]|[ІЇЄiїєЁё]|[0-9]|_)+"; // Регулярка для проверки валидации
    private final LoginView loginView; // View регистрации
    private final UserModel userModel; // Модель пользователя, нужна для логина
    private final UserData userData;
    private final Context context;

    public LoginPresenter(LoginView loginView, Context context) {
        this.loginView = loginView;
        this.userModel = UserModel.getUserModel();
        this.userData = UserData.getUserData();
        this.context = context;
    }

    /**
     * Попытка авторизации пользователя
     *
     * @param nickname - никнейм
     * @param password - пароль
     */
    public void tryLogin(String nickname, String password) {
        if (!nickname.matches(validRegEx) || !password.matches(validRegEx)) {
            this.loginView.showToastError(context.getString(R.string.invalid_data));
        } else {
            this.userModel.login(nickname, password, new ExCallable<User>() {
                @Override
                public void call(User data) {
                    try {
                        userData.setUser(data);
                        loginView.openMain();
                    } catch (Exception err) {
                        err.printStackTrace();
                        loginView.showToastError(err.getMessage());
                    }
                }

                @Override
                public void fail(int idResString) {
                    try {
                        loginView.showToastError(context.getString(idResString));
                    } catch (Exception err) {
                        err.printStackTrace();
                        loginView.showToastError(err.getMessage());
                    }
                }
            });
        }
    }

    public void loginAsGuest() {
        userData.isGuest = true;
        userData.setUser(new User());
        loginView.openMain();
    }
}
