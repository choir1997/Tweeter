package edu.byu.cs.tweeter.client.presenter;

import java.net.MalformedURLException;
import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.observer.PagedObserver;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedPresenter<T> {
    private static final int PAGE_SIZE = 10;
    private final PagedView<T> view;
    private final UserService userService;
    private T lastItem;
    private Boolean hasMorePages;
    private Boolean isLoading = false;

    //override method from Presenter?
    public PagedPresenter(PagedView<T> view) {
        this.view = view;
        userService = new UserService();
    }

    public interface PagedView<U> extends BaseView {
        void setLoadingStatus(boolean value) throws MalformedURLException;
        void addItems(List<U> items);
        void displayUserProfile(User user);
    }

    public Boolean hasMorePages() {
        return hasMorePages;
    }

    public void setHasMorePages(Boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
    }

    public Boolean isLoading() {
        return isLoading;
    }

    public void getUser(String userAlias) {
        view.displayMessage("Getting user's profile...");
        userService.getUser(Cache.getInstance().getCurrUserAuthToken(), userAlias, new GetUserObserver());
    }

    public class GetUserObserver implements UserService.GetUserObserver {
        @Override
        public void handleSuccess(User user) {
            view.displayUserProfile(user);
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to get user's profile: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.displayMessage("Failed to get user's profile because of exception: " + exception.getMessage());
        }
    }

    public void loadMoreItems(User user) throws MalformedURLException {
        if (!isLoading) {   // This guard is important for avoiding a race condition in the scrolling code.
            isLoading = true;
            view.setLoadingStatus(true);
            getItems(user, PAGE_SIZE, lastItem);
        }
    }

    public abstract void getItems(User user, int pageSize, T lastItem);

    public class GetPagedObserver implements PagedObserver<T> {
        private final String preFailureMessage;
        private final String preExceptionMessage;

        public GetPagedObserver(String preFailureMessage, String preExceptionMessage) {
            this.preFailureMessage = preFailureMessage;
            this.preExceptionMessage = preExceptionMessage;
        }

        @Override
        public void handleSuccess(List<T> items, boolean hasMorePages) throws MalformedURLException {
            isLoading = false;
            view.setLoadingStatus(false);
            lastItem = (items.size() > 0) ? items.get(items.size() - 1) : null;
            setHasMorePages(hasMorePages);
            view.addItems(items);
        }

        @Override
        public void handleFailure(String message) throws MalformedURLException {
            isLoading = false;
            view.setLoadingStatus(false);
            view.displayMessage(preFailureMessage + message);
        }

        @Override
        public void handleException(Exception exception) throws MalformedURLException {
            isLoading = false;
            view.setLoadingStatus(false);
            view.displayMessage(preExceptionMessage + exception.getMessage());
        }
    }

}
