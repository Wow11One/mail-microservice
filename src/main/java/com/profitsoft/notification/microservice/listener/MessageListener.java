package com.profitsoft.notification.microservice.listener;

/**
 * The interface for a message listener
 */
public interface MessageListener {
    void handleMailQueue(String message);
}
