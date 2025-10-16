package com.kifiya.promotion_quoter.features.promotion.dto.request;

import com.kifiya.promotion_quoter.features.promotion.enums.PromotionType;
import jakarta.persistence.Column;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PromotionRequest(
        @NotNull(message = "Promotion Type is required")
        PromotionType type,
        String category,
        @DecimalMin(value = "0.0", message = "Percent must be at least 0.0")
        @DecimalMax(value = "100.0", message = "Percent must be at most 100.0")
        Double percent,
        String productId,
        @Min(value = 1, message = "Buy Item must not be negative")
        Integer x,
        @Min(value = 0, message = "Get free item must not be negative")
        Integer y,
        @Min(value = 0, message = "Order priority must not be negative")
        int orderPriority
) {
}
