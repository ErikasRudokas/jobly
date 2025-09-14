package com.jobly.exception.specific;

public class IncorrectTokenUsageException extends RuntimeException {
    public IncorrectTokenUsageException(String message) {
        super(message);
    }
}
