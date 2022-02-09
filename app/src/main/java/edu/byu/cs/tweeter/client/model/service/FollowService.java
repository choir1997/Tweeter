package edu.byu.cs.tweeter.client.model.service;

import android.os.Bundle;

import java.net.MalformedURLException;
import java.util.List;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.PagedTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.BackgroundTaskHandler;
import edu.byu.cs.tweeter.client.model.service.observer.MainObserver;
import edu.byu.cs.tweeter.client.model.service.observer.PagedObserver;
import edu.byu.cs.tweeter.client.model.service.observer.ServiceObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowService {
    //Following and Followers
    public void getFollowing(AuthToken currUserAuthToken, User user, int pageSize, User lastFollowee, PagedObserver<User> getFollowingObserver) {
        GetFollowingTask getFollowingTask = new GetFollowingTask(currUserAuthToken, user, pageSize, lastFollowee, new GetFollowingFollowersHandler(getFollowingObserver));
        BackgroundTaskUtils.runTask(getFollowingTask);
    }

    public void getFollowers(AuthToken currUserAuthToken, User user, int pageSize, User lastFollower, PagedObserver<User> getFollowerObserver) {
        GetFollowersTask getFollowersTask = new GetFollowersTask(currUserAuthToken, user, pageSize, lastFollower, new GetFollowingFollowersHandler(getFollowerObserver));
        BackgroundTaskUtils.runTask(getFollowersTask);
    }

    private static class GetFollowingFollowersHandler extends BackgroundTaskHandler<PagedObserver<User>> {
        public GetFollowingFollowersHandler(PagedObserver<User> observer) {
            super(observer);
        }
        @Override
        protected void handleSuccessMessage(PagedObserver<User> observer, Bundle data) throws MalformedURLException {
            List<User> items = (List<User>) data.getSerializable(PagedTask.ITEMS_KEY);
            boolean hasMorePages = data.getBoolean(GetFollowingTask.MORE_PAGES_KEY);
            observer.handleSuccess(items, hasMorePages);
        }
    }

    //Get Follower and Following Count
    public void runFollowerAndFollowingCount(AuthToken currUserAuthToken, User selectedUser, MainObserver getFollowerObserver, MainObserver getFollowingObserver) {
        //ExecutorService executor = Executors.newFixedThreadPool(2);
        GetFollowersCountTask followersCountTask = new GetFollowersCountTask(currUserAuthToken, selectedUser, new GetFollowersCountHandler(getFollowerObserver));
        BackgroundTaskUtils.runTask(followersCountTask);

        GetFollowingCountTask followingCountTask = new GetFollowingCountTask(currUserAuthToken, selectedUser, new GetFollowingCountHandler(getFollowingObserver));
        BackgroundTaskUtils.runTask(followingCountTask);
    }

    private static class GetFollowersCountHandler extends BackgroundTaskHandler<MainObserver> {
        public GetFollowersCountHandler(MainObserver observer) {
            super(observer);
        }
        @Override
        protected void handleSuccessMessage(MainObserver observer, Bundle data) {
            int count = data.getInt(GetFollowersCountTask.COUNT_KEY);
            observer.handleSuccess(count, true);
        }
    }
    private static class GetFollowingCountHandler extends BackgroundTaskHandler<MainObserver> {
        public GetFollowingCountHandler(MainObserver observer) {
            super(observer);
        }
        @Override
        protected void handleSuccessMessage(MainObserver observer, Bundle data) {
            int count = data.getInt(GetFollowingCountTask.COUNT_KEY);
            observer.handleSuccess(count, false);
        }
    }

    //Is Follow
    public interface GetIsFollowerObserver extends ServiceObserver {
        void handleSuccess(boolean isFollower);
    }

    public void runIsFollower(AuthToken currUserAuthToken, User currUser, User selectedUser, GetIsFollowerObserver getIsFollowerObserver) {
        IsFollowerTask isFollowerTask = new IsFollowerTask(currUserAuthToken, currUser, selectedUser, new IsFollowerHandler(getIsFollowerObserver));
        BackgroundTaskUtils.runTask(isFollowerTask);
    }

    private static class IsFollowerHandler extends BackgroundTaskHandler<GetIsFollowerObserver> {
        public IsFollowerHandler(GetIsFollowerObserver observer) {
            super(observer);
        }

        @Override
        protected void handleSuccessMessage(GetIsFollowerObserver observer, Bundle data) {
            boolean isFollower = data.getBoolean(IsFollowerTask.IS_FOLLOWER_KEY);
            observer.handleSuccess(isFollower);
        }
    }

    //Follow/Unfollow
    public void runFollow(AuthToken currUserAuthToken, User selectedUser, MainObserver getFollowObserver) {
        FollowTask followTask = new FollowTask(currUserAuthToken, selectedUser, new FollowUnfollowHandler(getFollowObserver));
        BackgroundTaskUtils.runTask(followTask);
    }

    public void runUnfollow(AuthToken currUserAuthToken, User selectedUser, MainObserver getUnfollowObserver) {
        UnfollowTask unfollowTask = new UnfollowTask(currUserAuthToken, selectedUser, new FollowUnfollowHandler(getUnfollowObserver));
        BackgroundTaskUtils.runTask(unfollowTask);
    }

    private static class FollowUnfollowHandler extends BackgroundTaskHandler<MainObserver> {
        public FollowUnfollowHandler(MainObserver observer) {
            super(observer);
        }
        @Override
        protected void handleSuccessMessage(MainObserver observer, Bundle data) {
            observer.handleSuccess();
            observer.handleEnableFollowButton();
        }
    }
}
