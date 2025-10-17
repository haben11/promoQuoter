package com.kifiya.promotion_quoter.features.product.exception;

import com.kifiya.promotion_quoter.shared.exceptions.base.InsufficientStockException;

public class InsufficientProductException extends InsufficientStockException {
    public InsufficientProductException(String message) {
        super(message);
    }
}
