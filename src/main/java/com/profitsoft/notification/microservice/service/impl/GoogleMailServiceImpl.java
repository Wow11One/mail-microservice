package com.profitsoft.notification.microservice.service.impl;

import com.profitsoft.notification.microservice.exception.MailException;
import com.profitsoft.notification.microservice.service.MailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class GoogleMailServiceImpl implements MailService {

    final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    String senderEmail;

    @Override
    public void sendEmail(String from, String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(senderEmail);

            // currently I don't have users in my app, so that's why I send emails to myself
            // this logic will be changed in the future
            message.setTo(senderEmail);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception exception) {
            throw new MailException(
                    exception.getMessage(),
                    exception.getClass().getSimpleName()
            );
        }
    }
}
