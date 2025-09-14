package com.jobly.exception.specific;

public class NotUniqueEmailException extends RuntimeException {
    public NotUniqueEmailException(String message) {
        super(message);
    }
}
