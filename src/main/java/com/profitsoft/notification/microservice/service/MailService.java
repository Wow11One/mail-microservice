package com.profitsoft.notification.microservice.service;

/**
 * The interface that declares methods for mail services classes
 */
public interface MailService {

    void sendEmail(String from, String to, String subject, String body);
}
