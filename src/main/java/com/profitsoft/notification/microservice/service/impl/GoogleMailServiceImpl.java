package com.profitsoft.notification.microservice.service.impl;

import com.profitsoft.notification.microservice.exception.MailException;
import com.profitsoft.notification.microservice.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GoogleMailServiceImpl implements MailService {
    @Override
    public void sendEmail(String from, String to, String topic, String body) {
        try {
            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        } catch (Exception exception) {
            log.error("error occurred during the email sending {}", exception.getMessage());
            throw new MailException(
                    exception.getClass().getSimpleName(),
                    exception.getMessage()
            );
        }
    }
}
