package edu.byu.cs.tweeter.client.model.service.observer;

public interface MainObserver extends ServiceObserver {
    void handleSuccess();
    void handleEnableFollowButton();
    void handleSuccess(int count, boolean isFollowerCount);
}
