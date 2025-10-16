package com.kifiya.promotion_quoter.features.product.controller;

import com.kifiya.promotion_quoter.features.product.dto.request.ProductRequest;
import com.kifiya.promotion_quoter.features.product.dto.response.ProductResponse;
import com.kifiya.promotion_quoter.features.product.mapper.ProductMapper;
import com.kifiya.promotion_quoter.features.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request){
        return ResponseEntity.ok(productService.createProduct(request));
    }
}
