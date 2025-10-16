package com.kifiya.promotion_quoter.features.product.service;

import com.kifiya.promotion_quoter.features.product.dto.request.ProductRequest;
import com.kifiya.promotion_quoter.features.product.dto.response.ProductResponse;

public interface ProductService {
    ProductResponse createProduct(ProductRequest request);
}
