package com.kifiya.promotion_quoter.shared.exceptions.base;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) {
        super(message);
    }
}
