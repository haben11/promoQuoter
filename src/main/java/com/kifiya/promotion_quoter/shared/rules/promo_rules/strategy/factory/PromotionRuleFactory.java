package com.kifiya.promotion_quoter.shared.rules.promo_rules.strategy.factory;

import com.kifiya.promotion_quoter.features.promotion.model.Promotion;
import com.kifiya.promotion_quoter.shared.rules.promo_rules.PromotionRule;
import com.kifiya.promotion_quoter.shared.rules.promo_rules.strategy.impl.BuyXGetYRule;
import com.kifiya.promotion_quoter.shared.rules.promo_rules.strategy.impl.PercentOffCategoryRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PromotionRuleFactory {

    private final PercentOffCategoryRule percentOffCategoryRule;
    private final BuyXGetYRule buyXGetYRule;

    public PromotionRule getRule(Promotion promotion) {
        return switch (promotion.getType()) {
            case PERCENT_OFF_CATEGORY -> percentOffCategoryRule;
            case BUY_X_GET_Y -> buyXGetYRule;
        };
    }
}
