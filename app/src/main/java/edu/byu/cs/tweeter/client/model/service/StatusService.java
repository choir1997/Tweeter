package edu.byu.cs.tweeter.client.model.service;

import android.os.Bundle;

import java.net.MalformedURLException;
import java.util.List;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.PagedTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.PostStatusTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.BackgroundTaskHandler;
import edu.byu.cs.tweeter.client.model.service.observer.MainObserver;
import edu.byu.cs.tweeter.client.model.service.observer.PagedObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StatusService {
    //Feed/Story Observer and Handler
    public void getFeed(AuthToken currUserAuthToken, User user, int pageSize, Status lastStatus, PagedObserver<Status> getFeedObserver) {
        GetFeedTask getFeedTask = new GetFeedTask(currUserAuthToken, user, pageSize, lastStatus, new GetFeedStoryHandler(getFeedObserver));
        BackgroundTaskUtils.runTask(getFeedTask);
    }

    public void getStory(AuthToken currUserAuthToken, User user, int pageSize, Status lastStatus, PagedObserver<Status> getStoryObserver) {
        GetStoryTask getStoryTask = new GetStoryTask(currUserAuthToken, user, pageSize, lastStatus, new GetFeedStoryHandler(getStoryObserver));
        BackgroundTaskUtils.runTask(getStoryTask);
    }

    private static class GetFeedStoryHandler extends BackgroundTaskHandler<PagedObserver<Status>> {
        public GetFeedStoryHandler(PagedObserver<Status> observer) {
            super(observer);
        }
        @Override
        protected void handleSuccessMessage(PagedObserver<Status> observer, Bundle data) {
            List<Status> statuses = (List<Status>) data.getSerializable(PagedTask.ITEMS_KEY);
            boolean hasMorePages = data.getBoolean(GetFeedTask.MORE_PAGES_KEY);
            try {
                observer.handleSuccess(statuses, hasMorePages);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }
    //Post Status observer, Handler
    public void runPostStatus(AuthToken currUserAuthToken, Status newStatus, MainObserver getStatusObserver) {
        PostStatusTask statusTask = new PostStatusTask(currUserAuthToken,
                newStatus, new PostStatusHandler(getStatusObserver));
        BackgroundTaskUtils.runTask(statusTask);
    }

    private static class PostStatusHandler extends BackgroundTaskHandler<MainObserver> {
        public PostStatusHandler(MainObserver observer) {
            super(observer);
        }
        @Override
        protected void handleSuccessMessage(MainObserver observer, Bundle data) {
            observer.handleSuccess();
        }
    }
}
