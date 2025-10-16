package com.kifiya.promotion_quoter.features.promotion.exception;

public enum PromotionExceptionMessages {
    PROMOTION_NOT_FOUND("Promotion not found");

    private final String message;

    PromotionExceptionMessages(String message) {
        this.message = message;
    }
}
