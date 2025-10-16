package com.kifiya.promotion_quoter.shared.exceptions.base;

public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
