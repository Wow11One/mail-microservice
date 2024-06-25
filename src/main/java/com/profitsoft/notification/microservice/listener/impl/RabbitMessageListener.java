package com.profitsoft.notification.microservice.listener.impl;

import com.profitsoft.notification.microservice.constants.RabbitMqQueues;
import com.profitsoft.notification.microservice.listener.MessageListener;
import com.profitsoft.notification.microservice.service.impl.RabbitMessageListenerService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RabbitMessageListener implements MessageListener {

    RabbitMessageListenerService messageListenerService;

    @Override
    @RabbitListener(queues = {RabbitMqQueues.MAIL_QUEUE})
    public void handleMailQueue(String message) {
        messageListenerService.handleMessage(message);
    }
}
