package com.amit.spring.exception;

public sealed class AppException extends RuntimeException permits ProductNotFoundException, IllegalDbOperation, RedisPutException{
    public AppException (String message){
        super(message);
    }
    public AppException (String message, Throwable cause){
        super(message,cause);
    }
}
