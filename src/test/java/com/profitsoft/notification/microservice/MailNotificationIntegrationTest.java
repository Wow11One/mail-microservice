package com.profitsoft.notification.microservice;

import com.profitsoft.notification.microservice.config.TestElasticsearchConfiguration;
import com.profitsoft.notification.microservice.entity.MailMessage;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@ActiveProfiles({"test"})
@FieldDefaults(level = AccessLevel.PRIVATE)
@ContextConfiguration(classes = {TestElasticsearchConfiguration.class})
@Testcontainers
public class MailNotificationIntegrationTest {

    @MockBean
    JavaMailSenderImpl javaMailSender;

    @Autowired
    ElasticsearchOperations operations;

    @Test
    public void test() {

        MailMessage mailMessage = new MailMessage();
        operations.save(mailMessage);
        System.out.println(operations.get(mailMessage.getId(), MailMessage.class));
    }
}
