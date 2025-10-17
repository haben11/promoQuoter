package com.kifiya.promotion_quoter.features.product.exception;

public enum ProductExceptionMessages {
    PRODUCT_NOT_FOUND("Product not found"),
    PRODUCT_ALREADY_EXISTS("Product with the provided details already exists");

    private final String message;

    ProductExceptionMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
