package edu.byu.cs.tweeter.client.model.service;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.BackgroundTaskHandler;
import edu.byu.cs.tweeter.client.model.service.observer.LoginRegisterObserver;
import edu.byu.cs.tweeter.client.model.service.observer.MainObserver;
import edu.byu.cs.tweeter.client.model.service.observer.ServiceObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class UserService {
    //Get user
    public interface GetUserObserver extends ServiceObserver {
        void handleSuccess(User user);
    }

    public void getUser(AuthToken currUserAuthToken, String userAlias, GetUserObserver getUserObserver) {
        GetUserTask getUserTask = new GetUserTask(currUserAuthToken, userAlias, new GetUserHandler(getUserObserver));
        BackgroundTaskUtils.runTask(getUserTask);
    }

    private class GetUserHandler extends BackgroundTaskHandler<GetUserObserver> {
        public GetUserHandler(GetUserObserver observer) {
            super(observer);
        }

        @Override
        protected void handleSuccessMessage(GetUserObserver observer, Bundle data) {
            User user = (User) data.getSerializable(GetUserTask.USER_KEY);
            observer.handleSuccess(user);
        }
    }

    //Login
    public void runLogin(String alias, String password, LoginRegisterObserver getLoginObserver) {
        LoginTask loginTask = new LoginTask(alias, password, new LoginHandler(getLoginObserver));
        BackgroundTaskUtils.runTask(loginTask);
    }

    private static class LoginHandler extends BackgroundTaskHandler<LoginRegisterObserver>{
        public LoginHandler(LoginRegisterObserver observer) {
            super(observer);
        }
        @Override
        protected void handleSuccessMessage(LoginRegisterObserver observer, Bundle data) {
            User loggedInUser = (User) data.getSerializable(LoginTask.USER_KEY);
            AuthToken authToken = (AuthToken) data.getSerializable(LoginTask.AUTH_TOKEN_KEY);
            observer.handleSuccess(loggedInUser, authToken);
        }
    }

    //Register
    public void runRegister(String firstName, String lastName, String alias, String password, String imageBytesBase64, LoginRegisterObserver getRegisterObserver) {
        RegisterTask registerTask = new RegisterTask(firstName, lastName,
                alias, password, imageBytesBase64, new RegisterHandler(getRegisterObserver));
        BackgroundTaskUtils.runTask(registerTask);
    }

    private static class RegisterHandler extends BackgroundTaskHandler<LoginRegisterObserver> {
        public RegisterHandler(LoginRegisterObserver observer) {
            super(observer);
        }
        @Override
        protected void handleSuccessMessage(LoginRegisterObserver observer, Bundle data) {
            User registeredUser = (User) data.getSerializable(RegisterTask.USER_KEY);
            AuthToken authToken = (AuthToken) data.getSerializable(RegisterTask.AUTH_TOKEN_KEY);
            observer.handleSuccess(registeredUser, authToken);
        }
    }

    //Logout
    public void runLogout(AuthToken currUserAuthToken, MainObserver getLogoutObserver) {
        LogoutTask logoutTask = new LogoutTask(currUserAuthToken, new LogoutHandler(getLogoutObserver));
        BackgroundTaskUtils.runTask(logoutTask);
    }

    private static class LogoutHandler extends BackgroundTaskHandler<MainObserver> {
        public LogoutHandler(MainObserver observer) {
            super(observer);
        }
        @Override
        protected void handleSuccessMessage(MainObserver observer, Bundle data) {
            observer.handleSuccess();
        }
    }
}
