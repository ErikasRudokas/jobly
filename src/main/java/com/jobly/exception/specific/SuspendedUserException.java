package com.jobly.exception.specific;

public class SuspendedUserException extends RuntimeException {
    public SuspendedUserException(String message) {
        super(message);
    }
}

