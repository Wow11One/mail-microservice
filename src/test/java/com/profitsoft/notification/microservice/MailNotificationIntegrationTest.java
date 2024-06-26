package com.profitsoft.notification.microservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.profitsoft.notification.microservice.config.TestElasticsearchConfiguration;
import com.profitsoft.notification.microservice.constants.MailMessageStatus;
import com.profitsoft.notification.microservice.dto.MailDto;
import com.profitsoft.notification.microservice.entity.MailMessage;
import com.profitsoft.notification.microservice.exception.MailException;
import com.profitsoft.notification.microservice.repository.MailMessageRepository;
import com.profitsoft.notification.microservice.service.impl.RabbitMessageListenerService;
import com.profitsoft.notification.microservice.task.ResendFailedEmailsTask;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.util.Streamable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@Testcontainers
@ContextConfiguration(classes = {TestElasticsearchConfiguration.class})
@ActiveProfiles({"test"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MailNotificationIntegrationTest {

    @Container
    @ServiceConnection
    public static RabbitMQContainer rabbitMQContainer = new RabbitMQContainer("rabbitmq:3.12");

    @MockBean
    JavaMailSenderImpl javaMailSender;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    MailMessageRepository mailMessageRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ResendFailedEmailsTask resendFailedEmailsTask;


    @AfterEach
    public void tearDown() {
        mailMessageRepository.deleteAll();
    }

    @Test
    public void testThatAfterSuccessfulSending_EmailIsSavedToDb() throws Exception {
        List<MailMessage> initialEmailList = Streamable.of(mailMessageRepository.findAll())
                .toList();

        assertThat(initialEmailList).isNotNull();
        assertThat(initialEmailList).isEmpty();

        MailDto mailDto = new MailDto(
                "admin@mail.com",
                "Topic",
                "Hello world!"
        );

        // not sending a real email to allow the app to save mail message record to the db
        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));

        // simulating a rabbitmq message sending, invoking a method annotated with a RabbitListener annotation
        rabbitTemplate.convertAndSend("mail.queue", objectMapper.writeValueAsString(mailDto));
        Thread.sleep(1000);

        List<MailMessage> actualEmailList = Streamable.of(mailMessageRepository.findAll())
                .toList();

        assertThat(actualEmailList).isNotNull();
        assertThat(actualEmailList).isNotEmpty();

        MailMessage sentMessage = actualEmailList.get(0);

        assertThat(sentMessage).isNotNull();
        assertThat(sentMessage.getReceiver()).isEqualTo(mailDto.getReceiver());
        assertThat(sentMessage.getSubject()).isEqualTo(mailDto.getTopic());
        assertThat(sentMessage.getBody()).isEqualTo(mailDto.getBody());
        assertThat(sentMessage.getStatus()).isEqualTo(MailMessageStatus.SUCCESSFUL.toString());
        assertThat(sentMessage.getFailedAttemptsCount()).isZero();
        assertThat(sentMessage.getErrorMessage()).isNull();
    }

    @Test
    public void testThatAfterFailedSending_EmailIsSavedToDbWithErrorMessage() throws Exception {
        List<MailMessage> initialEmailList = Streamable.of(mailMessageRepository.findAll())
                .toList();

        assertThat(initialEmailList).isNotNull();
        assertThat(initialEmailList).isEmpty();

        MailDto mailDto = new MailDto(
                "admin@mail.com",
                "Topic",
                "Hello world!"
        );

        // simulates an error
        String errorMessage = "some test error message";
        RuntimeException runtimeException = new RuntimeException(errorMessage);
        String errorClassName = runtimeException.getClass().getSimpleName();
        doThrow(runtimeException).when(javaMailSender).send(any(SimpleMailMessage.class));

        // simulating a rabbitmq message sending, invoking a method annotated with a RabbitListener annotation
        rabbitTemplate.convertAndSend("mail.queue", objectMapper.writeValueAsString(mailDto));
        Thread.sleep(1000);

        List<MailMessage> actualEmailList = Streamable.of(mailMessageRepository.findAll())
                .toList();

        assertThat(actualEmailList).isNotNull();
        assertThat(actualEmailList).isNotEmpty();

        MailMessage sentMessage = actualEmailList.get(0);

        assertThat(sentMessage).isNotNull();
        assertThat(sentMessage.getReceiver()).isEqualTo(mailDto.getReceiver());
        assertThat(sentMessage.getSubject()).isEqualTo(mailDto.getTopic());
        assertThat(sentMessage.getBody()).isEqualTo(mailDto.getBody());

        assertThat(sentMessage.getStatus()).isEqualTo(MailMessageStatus.FAILED.toString());
        assertThat(sentMessage.getFailedAttemptsCount()).isOne();
        assertThat(sentMessage.getErrorMessage()).isNotNull();
        assertThat(sentMessage.getErrorMessage()).contains(errorMessage);
        assertThat(sentMessage.getErrorMessage()).contains(errorClassName);
    }

    @Test
    public void testThatRabbitMessageWithIncorrectFormatWillNotBeSavedToDb() throws Exception {
        List<MailMessage> initialEmailList = Streamable.of(mailMessageRepository.findAll())
                .toList();

        assertThat(initialEmailList).isNotNull();
        assertThat(initialEmailList).isEmpty();

        // simulates an email sending
        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));

        // simulating a rabbitmq message sending, invoking a method annotated with a RabbitListener annotation
        String incorrectFormatMessage = "not correct message";
        rabbitTemplate.convertAndSend("mail.queue", incorrectFormatMessage);
        Thread.sleep(1000);

        List<MailMessage> actualEmailList = Streamable.of(mailMessageRepository.findAll())
                .toList();

        assertThat(actualEmailList).isNotNull();
        assertThat(actualEmailList).isEmpty();
    }

    @Test
    public void testThatAllFailedMessagesWillBeResentByTaskIfMailServerIsOk() {
        int initialFailedAttemptsCount = 2;
        saveFailedEmailsToDb(initialFailedAttemptsCount);

        List<MailMessage> initialEmailList = Streamable.of(mailMessageRepository.findAll())
                .toList();

        assertThat(initialEmailList).isNotNull();
        assertThat(initialEmailList).size().isEqualTo(3);

        // simulates an email sending
        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));

        // calling explicitly a scheduled task
        resendFailedEmailsTask.resendFailedEmails();

        List<MailMessage> actualEmailList = Streamable.of(mailMessageRepository.findAll())
                .toList();

        assertThat(actualEmailList).isNotNull();
        assertThat(actualEmailList).size().isEqualTo(3);
        actualEmailList.forEach(email -> {
            assertThat(email.getFailedAttemptsCount()).isEqualTo(initialFailedAttemptsCount);
            assertThat(email.getErrorMessage()).isNull();
            assertThat(email.getStatus()).isEqualTo(MailMessageStatus.SUCCESSFUL.toString());
        });
    }

    @Test
    public void testThatAllFailedMessagesWillNotBeResentByTaskIfMailServerFails() throws Exception {
        int initialFailedAttemptsCount = 2;
        saveFailedEmailsToDb(initialFailedAttemptsCount);

        List<MailMessage> initialEmailList = Streamable.of(mailMessageRepository.findAll())
                .toList();

        assertThat(initialEmailList).isNotNull();
        assertThat(initialEmailList).size().isEqualTo(3);

        // simulates an email sending error
        String errorMessage = "some test error message";
        RuntimeException runtimeException = new RuntimeException(errorMessage);
        String errorClassName = runtimeException.getClass().getSimpleName();
        doThrow(runtimeException).when(javaMailSender).send(any(SimpleMailMessage.class));

        // calling explicitly a scheduled task
        resendFailedEmailsTask.resendFailedEmails();

        List<MailMessage> actualEmailList = Streamable.of(mailMessageRepository.findAll())
                .toList();

        assertThat(actualEmailList).isNotNull();
        assertThat(actualEmailList).size().isEqualTo(3);
        actualEmailList.forEach(email -> {
            assertThat(email.getFailedAttemptsCount()).isEqualTo(initialFailedAttemptsCount + 1);
            assertThat(email.getErrorMessage()).isNotNull();
            assertThat(email.getErrorMessage()).contains(errorClassName);
            assertThat(email.getErrorMessage()).contains(errorMessage);
            assertThat(email.getStatus()).isEqualTo(MailMessageStatus.FAILED.toString());
        });
    }



    private void saveFailedEmailsToDb(int initialFailedAttemptsCount) {
        MailMessage mailMessageFirst = new MailMessage();
        mailMessageFirst.setReceiver("receiver");
        mailMessageFirst.setSender("sender");
        mailMessageFirst.setSubject("some subject");
        mailMessageFirst.setBody("some body");
        mailMessageFirst.setStatus(MailMessageStatus.FAILED.toString());
        mailMessageFirst.setFailedAttemptsCount(initialFailedAttemptsCount);
        mailMessageFirst.setErrorMessage("some message");

        MailMessage mailMessageSecond = new MailMessage();
        mailMessageSecond.setReceiver("second receiver");
        mailMessageSecond.setSender("second sender 2");
        mailMessageSecond.setSubject("some subject 2");
        mailMessageSecond.setBody("some body 2");
        mailMessageSecond.setStatus(MailMessageStatus.FAILED.toString());
        mailMessageSecond.setFailedAttemptsCount(initialFailedAttemptsCount);
        mailMessageSecond.setErrorMessage("some message 2");

        MailMessage mailMessageThird = new MailMessage();
        mailMessageThird.setReceiver("third receiver");
        mailMessageThird.setSender("third sender");
        mailMessageThird.setSubject("some subject 3");
        mailMessageThird.setBody("some body 3");
        mailMessageThird.setStatus(MailMessageStatus.FAILED.toString());
        mailMessageThird.setFailedAttemptsCount(initialFailedAttemptsCount);
        mailMessageThird.setErrorMessage("some message 3");

        mailMessageRepository.save(mailMessageFirst);
        mailMessageRepository.save(mailMessageSecond);
        mailMessageRepository.save(mailMessageThird);
    }
}
