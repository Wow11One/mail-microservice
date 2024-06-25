package com.profitsoft.notification.microservice.listener;

public interface MessageListener {
    void handleMailQueue(String message);
}
