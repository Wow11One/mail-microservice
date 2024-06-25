package com.profitsoft.notification.microservice.service.impl;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.profitsoft.notification.microservice.constants.MailMessageStatus;
import com.profitsoft.notification.microservice.dto.MailDto;
import com.profitsoft.notification.microservice.entity.MailMessage;
import com.profitsoft.notification.microservice.exception.MailException;
import com.profitsoft.notification.microservice.repository.MailMessageRepository;
import com.profitsoft.notification.microservice.service.MessageListenerService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
public class RabbitMessageListenerService implements MessageListenerService {

    final ObjectMapper objectMapper;
    final MailMessageRepository mailMessageRepository;
    final GoogleMailServiceImpl mailService;

    @Value("${spring.mail.username}")
    String senderEmail;

    @Override
    public void handleMessage(String message) {
        if (message == null) {
            log.error("Rabbit listener received a message with null value");
            return;
        }

        MailMessage mailMessage = new MailMessage();
        try {
            MailDto mailDto = objectMapper.readValue(message, MailDto.class);

            mailMessage.setSender(mailDto.getReceiver());
            mailMessage.setReceiver(mailDto.getReceiver());
            mailMessage.setSubject(mailDto.getTopic());
            mailMessage.setBody(mailDto.getBody());
            mailMessage.setLastAttemptTime(Instant.now());

            mailService.sendEmail(
                    mailDto.getReceiver(),
                    mailDto.getReceiver(),
                    mailDto.getTopic(),
                    mailDto.getBody()
            );

            mailMessage.setStatus(MailMessageStatus.SUCCESSFUL);
        } catch (MailException exception) {
            String errorMessage = exception.getFullMessage();
            log.error("Error occurred during the email sending: {}", errorMessage);

            mailMessage.setErrorMessage(errorMessage);
            mailMessage.setStatus(MailMessageStatus.FAILED);
            mailMessage.setFailedAttemptsCount(1);
        } catch (JacksonException exception) {
            log.error("Error occurred during a message deserialization: {}", exception.getMessage());
        } catch (Exception exception) {
            log.error("Unexpected error occurred in a message listener: {}. No message was saved",
                    exception.getMessage());
        } finally {
            if (mailMessage.getStatus() != null) {
                mailMessageRepository.save(mailMessage);
                log.info("Message with id #{} was saved to the db", mailMessage.getId());
            }
        }

    }
}
