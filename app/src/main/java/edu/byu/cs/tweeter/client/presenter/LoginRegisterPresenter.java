package edu.byu.cs.tweeter.client.presenter;

import java.net.MalformedURLException;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.observer.LoginRegisterObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class LoginRegisterPresenter {
    private LoginRegisterView view;

    public interface LoginRegisterView extends BaseView {
        void setErrorView(String message);
        void switchToProfile(User user);
    }

    public LoginRegisterPresenter(LoginRegisterView view) {
        this.view = view;
    }

    public void checkAliasAndPassword(String alias, String password) {
        if (alias.charAt(0) != '@') {
            throw new IllegalArgumentException("Alias must begin with @.");
        }
        if (alias.length() < 2) {
            throw new IllegalArgumentException("Alias must contain 1 or more characters after the @.");
        }
        if (password.length() == 0) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
    }

    public class GetLoginRegisterObserver implements LoginRegisterObserver {
        String preFailureMessage;
        String preExceptionMessage;

        public GetLoginRegisterObserver(String preFailureMessage, String preExceptionMessage) {
            this.preFailureMessage = preFailureMessage;
            this.preExceptionMessage = preExceptionMessage;
        }

        @Override
        public void handleSuccess(User user, AuthToken authToken) {
            Cache.getInstance().setCurrUser(user);
            Cache.getInstance().setCurrUserAuthToken(authToken);
            view.switchToProfile(user);
        }

        @Override
        public void handleFailure(String message) throws MalformedURLException {
            view.displayMessage(preFailureMessage + message);
        }

        @Override
        public void handleException(Exception exception) throws MalformedURLException {
            view.displayMessage(preExceptionMessage + exception.getMessage());
        }
    }




}
