package edu.byu.cs.tweeter.client.model.service.observer;

import java.net.MalformedURLException;

public interface ServiceObserver {
    void handleFailure(String message) throws MalformedURLException;
    void handleException(Exception exception) throws MalformedURLException;
}

