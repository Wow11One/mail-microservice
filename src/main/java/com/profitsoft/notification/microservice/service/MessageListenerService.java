package com.profitsoft.notification.microservice.service;

import com.profitsoft.notification.microservice.dto.MailDto;

public interface MessageListenerService {
    void handleMailQueue(String message);
}
