package com.profitsoft.notification.microservice.entity;

import com.profitsoft.notification.microservice.constants.MailMessageStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
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
@AllArgsConstructor
@Getter
@Setter
@ToString
public class MailMessage {

    @Id
    String id;

    @Field(type = FieldType.Text)
    String from;

    @Field(type = FieldType.Text)
    String to;

    @Field(type = FieldType.Integer)
    Integer sendAttempts;

    @Field(type = FieldType.Date)
    Instant sentAt;

    @Field(type = FieldType.Text)
    String errorMessage;

    @Field(type = FieldType.Text)
    MailMessageStatus status;
}
