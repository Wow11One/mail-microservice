package com.profitsoft.notification.microservice.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/**
 * The DTO class for json deserialization during receiving a message from RabbitMQ.
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MailDto {
    String receiver;
    String topic;
    String body;
}
