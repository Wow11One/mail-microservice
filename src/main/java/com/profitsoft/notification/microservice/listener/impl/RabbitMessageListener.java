package com.profitsoft.notification.microservice.listener.impl;

import com.profitsoft.notification.microservice.constants.RabbitMqQueues;
import com.profitsoft.notification.microservice.listener.MessageListener;
import com.profitsoft.notification.microservice.service.MessageListenerService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * The class that implements message listening logic, using RabbitMq.
 */
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RabbitMessageListener implements MessageListener {

    MessageListenerService messageListenerService;

    @Override
    @RabbitListener(queues = {RabbitMqQueues.MAIL_QUEUE})
    public void handleMailQueue(String message) {
        messageListenerService.handleMessage(message);
    }
}
