package com.profitsoft.notification.microservice.service.impl;

import com.profitsoft.notification.microservice.constants.RabbitMqQueues;
import com.profitsoft.notification.microservice.service.MessageListenerService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RabbitMessageListenerServiceImpl implements MessageListenerService {

    @RabbitListener(queues = {RabbitMqQueues.MAIL_QUEUE})
    public void handleMailQueue() {

    }
}
