package com.kifiya.promotion_quoter.features.promotion.controller;

import com.kifiya.promotion_quoter.features.promotion.dto.request.PromotionRequest;
import com.kifiya.promotion_quoter.features.promotion.dto.response.PromotionResponse;
import com.kifiya.promotion_quoter.features.promotion.service.PromotionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/promotions")
@RequiredArgsConstructor
@Tag(name = "Promotion Management", description = "Endpoints for managing promotions")
public class PromotionController {

    private final PromotionService promotionService;

    @PostMapping
    @Operation(summary = "Creates promotion",
            description = "Creates promotions")
    public ResponseEntity<PromotionResponse> createPromotion(@Valid @RequestBody PromotionRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(promotionService.createPromotion(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update promotion",
            description = "Updates promotion for swapping purpose")
    public ResponseEntity<PromotionResponse> updatePromotion(@PathVariable String id,@Valid @RequestBody PromotionRequest request){
        return ResponseEntity.ok(promotionService.updatePromotion(id,request));
    }
}
