package com.profitsoft.notification.microservice.entity;

import com.profitsoft.notification.microservice.constants.MailMessageStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;

@Document(indexName = "email_messages")
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Getter
@Setter
@ToString
public class MailMessage {

    @Id
    String id;

    @Field(type = FieldType.Text)
    String sender;

    @Field(type = FieldType.Text)
    String receiver;

    @Field(type = FieldType.Text)
    String subject;

    @Field(type = FieldType.Text)
    String body;

    @Field(type = FieldType.Integer)
    Integer failedAttemptsCount = 0;

    @Field(type = FieldType.Date)
    Instant createdAt = Instant.now();

    @Field(type = FieldType.Date)
    Instant lastAttemptTime;

    @Field(type = FieldType.Text)
    String errorMessage;

    @Field(type = FieldType.Text)
    MailMessageStatus status;
}
