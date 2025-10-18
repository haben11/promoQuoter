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
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository repository;
    private final PromotionMapper mapper;

    @Override
    public PromotionResponse createPromotion(PromotionRequest request) {

        Promotion entity = mapper.toEntity(request);
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
