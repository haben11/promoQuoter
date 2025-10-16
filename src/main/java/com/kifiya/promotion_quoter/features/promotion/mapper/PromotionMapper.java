package com.kifiya.promotion_quoter.features.promotion.mapper;

import com.kifiya.promotion_quoter.config.mapper.MapperConfig;
import com.kifiya.promotion_quoter.features.promotion.dto.request.PromotionRequest;
import com.kifiya.promotion_quoter.features.promotion.dto.response.PromotionResponse;
import com.kifiya.promotion_quoter.features.promotion.model.Promotion;
import com.kifiya.promotion_quoter.shared.mapper.BaseMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface PromotionMapper extends BaseMapper<Promotion, PromotionRequest, PromotionResponse> {
}
