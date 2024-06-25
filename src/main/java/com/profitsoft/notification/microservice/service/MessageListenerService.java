package com.profitsoft.notification.microservice.service;

public interface MessageListenerService {

    void handleMessage(String message);
}
