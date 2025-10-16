package com.kifiya.promotion_quoter.features.product.exception;

import com.kifiya.promotion_quoter.shared.exceptions.base.BaseException;
import org.springframework.http.HttpStatus;

public class InsufficientStockException extends RuntimeException {
    private final HttpStatus status;
    public InsufficientStockException(String message) {
        super(message);
        this.status = HttpStatus.CONFLICT;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
