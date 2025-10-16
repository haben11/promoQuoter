package com.kifiya.promotion_quoter.features.promotion.service;

import com.kifiya.promotion_quoter.features.promotion.dto.request.PromotionRequest;
import com.kifiya.promotion_quoter.features.promotion.dto.response.PromotionResponse;

public interface PromotionService {
    PromotionResponse createPromotion(PromotionRequest request);

    PromotionResponse updatePromotion(String id, PromotionRequest request);
}
