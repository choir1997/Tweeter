package edu.byu.cs.tweeter.client.presenter;

import android.util.Log;

import edu.byu.cs.tweeter.client.model.service.UserService;

public class LoginPresenter extends LoginRegisterPresenter {
    private final LoginRegisterView view;
    private final UserService userService;

    public LoginPresenter(LoginRegisterView view) {
        super(view);
        this.view = view;
        userService = new UserService();
    }

    public void validateAndLogin(String alias, String password) {
        try {
            checkAliasAndPassword(alias, password);
            view.setErrorView(null);
            view.displayMessage("Logging In...");
            userService.runLogin(alias, password, new GetLoginRegisterObserver("Failed to login: " , "Failed to login because of exception: "));
        } catch (Exception e) {
            view.setErrorView(e.getMessage());
            Log.e(LogTag.LOGIN_LOG_TAG, e.getMessage(), e);
        }
    }
}
