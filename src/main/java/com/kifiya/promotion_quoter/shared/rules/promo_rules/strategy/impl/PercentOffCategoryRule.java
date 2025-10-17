package com.kifiya.promotion_quoter.shared.rules.promo_rules.strategy.impl;

import com.kifiya.promotion_quoter.features.cart.dto.request.CartItem;
import com.kifiya.promotion_quoter.features.promotion.model.Promotion;
import com.kifiya.promotion_quoter.shared.rules.context.CartCalculationContext;
import com.kifiya.promotion_quoter.shared.rules.promo_rules.PromotionRule;
import com.kifiya.promotion_quoter.shared.utils.price.PriceUtil;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PercentOffCategoryRule implements PromotionRule {
    @Override
    public void apply(CartCalculationContext context, Promotion promotion) {
        String category = promotion.getCategory();
        double percent = promotion.getPercent() / 100.0;
        for (CartItem item : context.getItems()) {
            var product = context.getProducts().get(item.productId());
            if (product != null && category.equals(product.getCategory())) {
                BigDecimal originalPrice = context.getCurrentPrices().get(item.productId());
                BigDecimal discount = originalPrice.multiply(BigDecimal.valueOf(percent));
                BigDecimal newPrice = PriceUtil.roundUp(originalPrice.subtract(discount).max(BigDecimal.ZERO));
                context.getCurrentPrices().put(item.productId(), newPrice);
                context.addAudit("Applied " + getDescription(promotion) + " to item " + item.productId() + ": discount " + discount);
            }
        }
    }

    @Override
    public String getDescription(Promotion promotion) {
        return promotion.getPercent() + "% off on category " + promotion.getCategory();
    }
}
