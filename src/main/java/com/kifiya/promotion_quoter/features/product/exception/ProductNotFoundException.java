package com.kifiya.promotion_quoter.features.product.exception;

import com.kifiya.promotion_quoter.shared.exceptions.base.ResourceNotFoundException;

public class ProductNotFoundException extends ResourceNotFoundException {
    public ProductNotFoundException() {
        super(ProductExceptionMessages.PRODUCT_NOT_FOUND.getMessage());
    }
}
