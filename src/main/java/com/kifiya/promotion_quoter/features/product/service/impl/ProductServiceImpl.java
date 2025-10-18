package com.kifiya.promotion_quoter.features.product.service.impl;

import com.kifiya.promotion_quoter.features.product.dto.request.ProductRequest;
import com.kifiya.promotion_quoter.features.product.dto.response.ProductResponse;
import com.kifiya.promotion_quoter.features.product.mapper.ProductMapper;
import com.kifiya.promotion_quoter.features.product.model.Product;
import com.kifiya.promotion_quoter.features.product.repository.ProductRepository;
import com.kifiya.promotion_quoter.features.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper mapper;

    @Override
    public ProductResponse createProduct(ProductRequest request) {

        Product entity = mapper.toEntity(request);
        return mapper.toBo(productRepository.save(entity));
    }
}
