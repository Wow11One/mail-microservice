package com.profitsoft.notification.microservice.service;

public interface MailService {

    void sendEmail(String from, String to, String topic, String body);
}
