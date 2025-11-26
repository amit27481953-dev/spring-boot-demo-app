package com.amit.spring.exception;

public final class RedisPutException extends AppException{
    public RedisPutException(String message) {
        super(message);
    }

    public RedisPutException(String message, Throwable cause) {
        super(message, cause);
    }
}
