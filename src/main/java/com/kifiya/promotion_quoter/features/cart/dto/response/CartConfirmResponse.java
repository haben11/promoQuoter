package com.kifiya.promotion_quoter.features.cart.dto.response;

import java.math.BigDecimal;

public record CartConfirmResponse(
        BigDecimal finalPrice,
        String orderId
) {
}
