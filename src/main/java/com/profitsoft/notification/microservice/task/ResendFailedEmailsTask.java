package com.profitsoft.notification.microservice.task;

import com.profitsoft.notification.microservice.constants.MailMessageStatus;
import com.profitsoft.notification.microservice.entity.MailMessage;
import com.profitsoft.notification.microservice.exception.MailException;
import com.profitsoft.notification.microservice.repository.MailMessageRepository;
import com.profitsoft.notification.microservice.service.MailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class ResendFailedEmailsTask {

    MailService mailService;
    MailMessageRepository mailMessageRepository;
    Integer BATCH_SIZE = 100;

    @Scheduled(cron = "${scheduler.cron-expr}")
    public void resendFailedEmails() {
        int currentPage = 0;
        int resentEmails = 0;
        int totalFailedEmailsAmount = 0;
        Pageable pageable;
        List<MailMessage> failedEmailslist;

        while (true) {
            pageable = PageRequest.of(
                    currentPage++,
                    BATCH_SIZE,
                    Sort.by(Sort.Order.asc("lastAttemptTime"))
            );

            // search by enum value did not work, so I replaced it with a string value
            failedEmailslist = mailMessageRepository.findByStatus(MailMessageStatus.FAILED.toString(), pageable)
                   .getContent();

            if (failedEmailslist.isEmpty()) {
                log.info(
                        """
                        The failed emails resending task is over.
                        {} out of {} emails resent successfully.
                        """,
                        resentEmails,
                        totalFailedEmailsAmount
                );
                break;
            }

            totalFailedEmailsAmount += failedEmailslist.size();
            for (MailMessage email : failedEmailslist) {
                if (resendEmail(email)) {
                    resentEmails++;
                }
            }
        }
    }

    private boolean resendEmail(MailMessage mailMessage) {
        try {
            mailMessage.setLastAttemptTime(Instant.now());
            mailService.sendEmail(
                    mailMessage.getSender(),
                    mailMessage.getReceiver(),
                    mailMessage.getSubject(),
                    mailMessage.getBody()
            );

            mailMessage.setStatus(MailMessageStatus.SUCCESSFUL.toString());
            mailMessage.setErrorMessage(null);
            mailMessageRepository.save(mailMessage);

            log.info("Email with id #{} resent successfully", mailMessage.getId());

            return true;
        } catch (MailException exception) {
            String errorMessage = exception.getFullMessage();
            log.error("Error occurred during an email resending:{}", errorMessage);

            mailMessage.setErrorMessage(errorMessage);
            mailMessage.setFailedAttemptsCount(mailMessage.getFailedAttemptsCount() + 1);
            mailMessageRepository.save(mailMessage);

            return false;
        }
    }
}
