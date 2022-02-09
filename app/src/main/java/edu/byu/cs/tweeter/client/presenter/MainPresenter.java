package edu.byu.cs.tweeter.client.presenter;

import android.util.Log;

import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.observer.MainObserver;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class MainPresenter {
    private final View view;
    private final StatusService statusService;
    private final UserService userService;
    private final FollowService followService;

    public interface View extends BaseView {
        void callLogout();
        void setCountText(int count, boolean setFollowerCount);
        void changeToIsFollowerButton();
        void changeToIsNotFollowerButton();
        void updateFollowInfo(boolean value);
        void enableFollowButton(boolean value);
    }

    public MainPresenter(View view) {
        this.view = view;
        statusService = new StatusService();
        userService = new UserService();
        followService = new FollowService();
    }

    public abstract class GetMainObserver implements MainObserver {
        private final String preFailureMessage;
        private final String preExceptionMessage;

        protected GetMainObserver(String preFailureMessage, String preExceptionMessage) {
            this.preFailureMessage = preFailureMessage;
            this.preExceptionMessage = preExceptionMessage;
        }

        public abstract void handleSuccess();

        public abstract void handleEnableFollowButton();

        public abstract void handleSuccess(int count, boolean isFollowerCount);

        @Override
        public void handleFailure(String message) {
            view.displayMessage(preFailureMessage + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.displayMessage(preExceptionMessage + exception.getMessage());
        }
    }

    public class GetStatusObserver extends GetMainObserver {
        protected GetStatusObserver(String preFailureMessage, String preExceptionMessage) {
            super(preFailureMessage, preExceptionMessage);
        }

        @Override
        public void handleSuccess() {
            view.displayMessage("Successfully Posted!");
        }

        @Override
        public void handleEnableFollowButton() {
        }

        @Override
        public void handleSuccess(int count, boolean isFollowerCount) {
        }
    }

    public void postStatus(String post) {
        try {
            Status newStatus = new Status(post, Cache.getInstance().getCurrUser(), getFormattedDateTime(), parseURLs(post), parseMentions(post));
            statusService.runPostStatus(Cache.getInstance().getCurrUserAuthToken(), newStatus, new GetStatusObserver("Failed to post status: ", "Failed to post status because of exception: "));
        } catch (Exception ex) {
            view.displayMessage("Failed to post the status because of exception: " + ex.getMessage());
            Log.e(LogTag.MAIN_LOG_TAG, ex.getMessage(), ex);
        }
    }

    public String getFormattedDateTime() throws ParseException {
        SimpleDateFormat userFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat statusFormat = new SimpleDateFormat("MMM d yyyy h:mm aaa");

        return statusFormat.format(userFormat.parse(LocalDate.now().toString() + " " + LocalTime.now().toString().substring(0, 8)));
    }

    public List<String> parseURLs(String post) throws MalformedURLException {
        List<String> containedUrls = new ArrayList<>();
        for (String word : post.split("\\s")) {
            if (word.startsWith("http://") || word.startsWith("https://")) {
                int index = findUrlEndIndex(word);
                word = word.substring(0, index);
                containedUrls.add(word);
            }
        }
        return containedUrls;
    }

    public List<String> parseMentions(String post) {
        List<String> containedMentions = new ArrayList<>();
        for (String word : post.split("\\s")) {
            if (word.startsWith("@")) {
                word = word.replaceAll("[^a-zA-Z0-9]", "");
                word = "@".concat(word);
                containedMentions.add(word);
            }
        }
        return containedMentions;
    }

    public int findUrlEndIndex(String word) {
        if (word.contains(".com")) {
            int index = word.indexOf(".com");
            index += 4;
            return index;
        } else if (word.contains(".org")) {
            int index = word.indexOf(".org");
            index += 4;
            return index;
        } else if (word.contains(".edu")) {
            int index = word.indexOf(".edu");
            index += 4;
            return index;
        } else if (word.contains(".net")) {
            int index = word.indexOf(".net");
            index += 4;
            return index;
        } else if (word.contains(".mil")) {
            int index = word.indexOf(".mil");
            index += 4;
            return index;
        } else {
            return word.length();
        }
    }

    //logout user
    public void logoutUser() {
        userService.runLogout(Cache.getInstance().getCurrUserAuthToken(), new GetLogoutObserver("Failed to logout: " , "Failed to logout because of exception: "));
    }

    public class GetLogoutObserver extends GetMainObserver {
        protected GetLogoutObserver(String preFailureMessage, String preExceptionMessage) {
            super(preFailureMessage, preExceptionMessage);
        }
        @Override
        public void handleSuccess() {
            view.callLogout();
        }

        @Override
        public void handleEnableFollowButton() {
        }

        @Override
        public void handleSuccess(int count, boolean isFollowerCount) {
        }
    }

    //Following and Followers Count
    public void getFollowerAndFollowingCount(User selectedUser) {
        followService.runFollowerAndFollowingCount(Cache.getInstance().getCurrUserAuthToken(), selectedUser,
                new GetFollowerFollowingObserver("Failed to get followers count: ",
                "Failed to get followers count because of exception: "),
                new GetFollowerFollowingObserver("Failed to get following count: ",
                        "Failed to get following count because of exception: "));
    }

    public class GetFollowerFollowingObserver extends GetMainObserver {
        protected GetFollowerFollowingObserver(String preFailureMessage, String preExceptionMessage) {
            super(preFailureMessage, preExceptionMessage);
        }
        @Override
        public void handleSuccess() { }
        @Override
        public void handleEnableFollowButton() { }
        @Override
        public void handleSuccess(int count, boolean isFollowerCount) {
            view.setCountText(count, isFollowerCount);
        }
    }

    //is Following
    public void getIsFollower(User selectedUser) {
        followService.runIsFollower(Cache.getInstance().getCurrUserAuthToken(), Cache.getInstance().getCurrUser(), selectedUser, new GetIsFollowerObserver());
    }

    public class GetIsFollowerObserver implements FollowService.GetIsFollowerObserver {

        @Override
        public void handleSuccess(boolean isFollower) {
            // If logged in user is a follower of the selected user, display the follow button as "following"
            if (isFollower) {
                view.changeToIsFollowerButton();
            } else {
                view.changeToIsNotFollowerButton();
            }
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to determine following relationship: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.displayMessage("Failed to determine following relationship because of exception: " + exception.getMessage());
        }
    }

    //Follow/Unfollow
    public void getFollow(User selectedUser) {
        followService.runFollow(Cache.getInstance().getCurrUserAuthToken(), selectedUser, new GetFollowObserver("Failed to follow: ",
                "Failed to follow because of exception: ", false));
        view.displayMessage("Adding " + selectedUser.getName() + "...");
    }

    public void getUnfollow(User selectedUser) {
        followService.runUnfollow(Cache.getInstance().getCurrUserAuthToken(), selectedUser, new GetFollowObserver("Failed to unfollow: ",
                "Failed to unfollow because of exception: ", true));
        view.displayMessage("Removing " + selectedUser.getName() + "...");
    }
    public class GetFollowObserver extends GetMainObserver {
        private final boolean value;

        protected GetFollowObserver(String preFailureMessage, String preExceptionMessage, boolean value) {
            super(preFailureMessage, preExceptionMessage);
            this.value = value;
        }
        @Override
        public void handleSuccess() {
            view.updateFollowInfo(value);
        }
        @Override
        public void handleEnableFollowButton() {
            view.enableFollowButton(true);
        }
        @Override
        public void handleSuccess(int count, boolean isFollowerCount) {
        }
    }
}
