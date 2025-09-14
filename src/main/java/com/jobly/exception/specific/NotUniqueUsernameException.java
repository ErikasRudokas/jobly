package com.jobly.exception.specific;

public class NotUniqueUsernameException extends RuntimeException {
    public NotUniqueUsernameException(String message) {
        super(message);
    }
}
