package com.kifiya.promotion_quoter.features.cart.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CartRequestDto(
        @NotEmpty(message = "Cart items must not be empty")
        @Size(min = 1, message = "Cart must have at least one item")
        List<CartItem> items,
        @NotNull(message = "Customer segment must not be null")
        @Size(max = 80, message = "Customer segment must be at most 80 characters")
        String customerSegment
) {}
