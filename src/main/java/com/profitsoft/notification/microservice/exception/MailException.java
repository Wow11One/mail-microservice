package com.profitsoft.notification.microservice.exception;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public class MailException extends RuntimeException {

    String className;

    public MailException(String message, String className) {
        super(message);
        this.className = className;
    }

    public String getFullMessage() {
        return """
                Error of %s class  occurred. Error message: %s
                """.formatted(className, super.getMessage());
    }
}
