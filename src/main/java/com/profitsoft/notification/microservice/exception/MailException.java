package com.profitsoft.notification.microservice.exception;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

/**
 * The wrapper exception class for any error that occurs during the email sending.
 * Method getFullMessage is used to specify the error causes and to save this text
 * to the db with a MailMessage entity.
 */
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
                error of %s class occurred. Error message:
                %s
               """.formatted(className, super.getMessage());
    }
}
