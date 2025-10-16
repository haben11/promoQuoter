package com.kifiya.promotion_quoter.features.cart.controller;

import com.kifiya.promotion_quoter.features.cart.dto.request.CartRequestDto;
import com.kifiya.promotion_quoter.features.cart.dto.response.CartConfirmResponse;
import com.kifiya.promotion_quoter.features.cart.dto.response.CartQuoteResponse;
import com.kifiya.promotion_quoter.features.cart.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/quote")
    public ResponseEntity<CartQuoteResponse> quote(@Valid @RequestBody CartRequestDto request) {
        return ResponseEntity.ok(cartService.quote(request));
    }

    @PostMapping("/confirm")
    public ResponseEntity<CartConfirmResponse> confirm(@Valid @RequestBody CartRequestDto request,
                                                       @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {
        return ResponseEntity.ok(cartService.confirm(request, idempotencyKey));
    }
}
