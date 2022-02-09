package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowingPresenter extends PagedPresenter<User> {
    private final FollowService followService;
    @Override
    public void getItems(User user, int pageSize, User lastItem) {
        followService.getFollowing(Cache.getInstance().getCurrUserAuthToken(), user, pageSize, lastItem, new GetPagedObserver("Failed to get feed: ", "Failed to get feed because of exception: "));
    }

    public FollowingPresenter(PagedView<User> view) {
        super(view);
        followService = new FollowService();
    }
}
