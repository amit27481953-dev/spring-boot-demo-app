package com.amit.spring.exception;

public final class ProductNotFoundException extends AppException {
    public ProductNotFoundException(String message) {
        super(message);
    }
    public ProductNotFoundException(String messsage, Throwable cause){
        super(messsage, cause);
    }
}
