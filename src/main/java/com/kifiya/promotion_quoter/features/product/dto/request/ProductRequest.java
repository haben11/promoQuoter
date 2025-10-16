package com.kifiya.promotion_quoter.features.product.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank(message = "Product name is required")
        String name,
        @NotBlank(message = "Product category is required")
        String category,
        @NotNull(message = "Product price is required")
        BigDecimal price,
        @Min(0)
        int stock
) {}
