package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresenter extends PagedPresenter<Status> {
    private final View view;
    private final StatusService statusService;

    public interface View extends PagedView<Status>{
        void switchToURL(String url);
    }

    public FeedPresenter(View view) {
        super(view);
        this.view = view;
        statusService = new StatusService();
    }

    @Override
    public void getItems(User user, int pageSize, Status lastItem) {
        statusService.getFeed(Cache.getInstance().getCurrUserAuthToken(), user, pageSize, lastItem, new GetPagedObserver("Failed to get feed: ", "Failed to get feed because of exception: "));
    }

    public void analyzeClickable(String clickable) {
        if (clickable.contains("http")) {
            view.switchToURL(clickable);
        } else {
            getUser(clickable);
        }
    }
}
