package com.kifiya.promotion_quoter.features.cart.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CartItem(
        @NotNull(message = "Product Id must not be Null")
        String productId,
        @Min(value = 1, message = "Quantity must be at least 1")
        int quantity
) {}
