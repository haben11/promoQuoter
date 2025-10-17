package com.kifiya.promotion_quoter.features.product.controller;

import com.kifiya.promotion_quoter.features.product.dto.request.ProductRequest;
import com.kifiya.promotion_quoter.features.product.dto.response.ProductResponse;
import com.kifiya.promotion_quoter.features.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Product Management", description = "Endpoints for managing products")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @Operation(summary = "Creates Product",
            description = "Endpoint for registering products")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(request));
    }
}
