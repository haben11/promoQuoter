package com.kifiya.promotion_quoter.features.cart.service;

import com.kifiya.promotion_quoter.features.cart.dto.request.CartRequestDto;
import com.kifiya.promotion_quoter.features.cart.dto.response.CartConfirmResponse;
import com.kifiya.promotion_quoter.features.cart.dto.response.CartQuoteResponse;

public interface CartService {
    CartQuoteResponse quote(CartRequestDto request);

    CartConfirmResponse confirm(CartRequestDto request, String idempotencyKey);
}
