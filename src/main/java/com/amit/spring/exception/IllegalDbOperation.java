package com.amit.spring.exception;

public final class IllegalDbOperation extends AppException{
    public IllegalDbOperation(String message) {
        super(message);
    }

    public IllegalDbOperation(String message, Throwable cause) {
        super(message, cause);
    }
}
