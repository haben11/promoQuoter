package com.kifiya.promotion_quoter.features.promotion.service.impl;

import com.kifiya.promotion_quoter.features.promotion.dto.request.PromotionRequest;
import com.kifiya.promotion_quoter.features.promotion.dto.response.PromotionResponse;
import com.kifiya.promotion_quoter.features.promotion.exception.PromotionNotFoundException;
import com.kifiya.promotion_quoter.features.promotion.mapper.PromotionMapper;
import com.kifiya.promotion_quoter.features.promotion.model.Promotion;
import com.kifiya.promotion_quoter.features.promotion.repository.PromotionRepository;
import com.kifiya.promotion_quoter.features.promotion.service.PromotionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository repository;
    private final PromotionMapper mapper;

    @Override
    public PromotionResponse createPromotion(PromotionRequest request) {

        Promotion entity = mapper.toEntity(request);

        //TODO always check uniqueness of each promotion and decide what to do if duplicate exists

        return mapper.toBo(repository.save(entity));
    }

    @Override
    public PromotionResponse updatePromotion(String id, PromotionRequest request) {
        return repository.findById(id)
                .map(promotion -> {
                    Promotion entity = mapper.updateEntity(request, promotion);
                    return mapper.toBo(repository.save(entity));
                })
                .orElseThrow(PromotionNotFoundException::new);
    }
}
