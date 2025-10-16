package com.kifiya.promotion_quoter.features.promotion.dto.response;

import com.kifiya.promotion_quoter.features.promotion.enums.PromotionType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PromotionResponse(
        String id,
        @NotNull(message = "Promotion Type must not be null")
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
        @NotNull(message = "Promotion priority must not be null")
        @Min(value = 0, message = "Order priority must not be negative")
        int orderPriority,
        boolean active
) {
}
