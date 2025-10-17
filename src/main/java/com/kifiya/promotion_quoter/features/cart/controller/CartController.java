package com.kifiya.promotion_quoter.features.cart.controller;

import com.kifiya.promotion_quoter.features.cart.dto.request.CartRequestDto;
import com.kifiya.promotion_quoter.features.cart.dto.response.CartConfirmResponse;
import com.kifiya.promotion_quoter.features.cart.dto.response.CartQuoteResponse;
import com.kifiya.promotion_quoter.features.cart.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@Tag(name = "Cart Management", description = "Endpoints for managing cart items quote and confirmation")
public class CartController {

    private final CartService cartService;

    @PostMapping("/quote")
    @Operation(summary = "Quote cart items based on the applied promotion",
            description = "Calculates quote of the cart items based on the pluggable promotions")
    public ResponseEntity<CartQuoteResponse> quote(@Valid @RequestBody CartRequestDto request) {
        return ResponseEntity.ok(cartService.quote(request));
    }

    @PostMapping("/confirm")
    @Operation(summary = "Confirm quoted items",
            description = "Confirms quoted cart items and reserves stock")
    public ResponseEntity<CartConfirmResponse> confirm(@Valid @RequestBody CartRequestDto request,
                                                       @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {
        return ResponseEntity.ok(cartService.confirm(request, idempotencyKey));
    }
}
