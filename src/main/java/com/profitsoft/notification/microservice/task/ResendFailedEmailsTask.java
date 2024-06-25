package com.profitsoft.notification.microservice.task;

import com.profitsoft.notification.microservice.repository.MailMessageRepository;
import com.profitsoft.notification.microservice.service.MailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class ResendFailedEmailsTask {

    MailService mailService;
    MailMessageRepository mailMessageRepository;

    @Scheduled(cron = "*/10 * * * * *")
    public void resendFailedEmails() {

    }
}
