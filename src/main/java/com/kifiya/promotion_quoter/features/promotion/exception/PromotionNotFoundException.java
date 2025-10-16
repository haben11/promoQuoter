package com.kifiya.promotion_quoter.features.promotion.exception;

import com.kifiya.promotion_quoter.shared.exceptions.base.ResourceNotFoundException;

public class PromotionNotFoundException extends ResourceNotFoundException {
    public PromotionNotFoundException() {
        super(PromotionExceptionMessages.PROMOTION_NOT_FOUND.name());
    }
}
