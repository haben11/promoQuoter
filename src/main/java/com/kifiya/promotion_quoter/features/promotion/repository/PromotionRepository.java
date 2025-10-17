package com.kifiya.promotion_quoter.features.promotion.repository;

import com.kifiya.promotion_quoter.features.promotion.model.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PromotionRepository extends JpaRepository<Promotion, String> {
    List<Promotion> findAllByActiveTrue();
}
