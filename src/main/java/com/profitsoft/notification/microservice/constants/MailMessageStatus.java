package com.profitsoft.notification.microservice.constants;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum MailMessageStatus {
    SUCCESSFUL,
    FAILED
}
