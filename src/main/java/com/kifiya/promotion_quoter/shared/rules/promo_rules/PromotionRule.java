package com.kifiya.promotion_quoter.shared.rules.promo_rules;

import com.kifiya.promotion_quoter.features.promotion.model.Promotion;
import com.kifiya.promotion_quoter.shared.rules.context.CartCalculationContext;

public interface PromotionRule {
    void apply(CartCalculationContext context, Promotion promotion);
    String getDescription(Promotion promotion);
}
