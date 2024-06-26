package com.profitsoft.notification.microservice.service;

/**
 * The interface that specifies method for a message listener service class
 */
public interface MessageListenerService {

    void handleMessage(String message);
}
