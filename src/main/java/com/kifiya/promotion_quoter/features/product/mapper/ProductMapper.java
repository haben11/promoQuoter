package com.kifiya.promotion_quoter.features.product.mapper;

import com.kifiya.promotion_quoter.config.mapper.MapperConfig;
import com.kifiya.promotion_quoter.features.product.dto.request.ProductRequest;
import com.kifiya.promotion_quoter.features.product.dto.response.ProductResponse;
import com.kifiya.promotion_quoter.features.product.model.Product;
import com.kifiya.promotion_quoter.shared.mapper.BaseMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface ProductMapper extends BaseMapper<Product, ProductRequest, ProductResponse> {
}
