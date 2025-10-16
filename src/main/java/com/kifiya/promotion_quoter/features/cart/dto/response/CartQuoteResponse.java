package com.kifiya.promotion_quoter.features.cart.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record CartQuoteResponse(
        Map<String, BigDecimal> itemPrices,
        BigDecimal totalPrice,
        List<String> appliedPromotions
) {
}
