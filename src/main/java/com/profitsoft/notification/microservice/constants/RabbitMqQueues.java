package com.profitsoft.notification.microservice.constants;

/**
 * The class that stores all rabbitmq queues' names as a final variable,
 * so it is possible to insert it into an annotation property (can't be done with enums)
 */
public class RabbitMqQueues {
    private RabbitMqQueues(){}

    public static final String MAIL_QUEUE = "mail.queue";
}
