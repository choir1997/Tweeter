package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowerPresenter extends PagedPresenter<User> {
    private final FollowService followService;

    public FollowerPresenter(PagedView<User> view) {
        super(view);
        followService = new FollowService();
    }
    @Override
    public void getItems(User user, int pageSize, User lastItem) {
        followService.getFollowers(Cache.getInstance().getCurrUserAuthToken(), user, pageSize, lastItem, new GetPagedObserver("Failed to get followers: ", "Failed to get followers because of exception: "));
    }

}
