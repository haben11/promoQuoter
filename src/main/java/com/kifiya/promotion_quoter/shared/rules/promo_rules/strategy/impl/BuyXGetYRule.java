package com.kifiya.promotion_quoter.shared.rules.promo_rules.strategy.impl;

import com.kifiya.promotion_quoter.features.cart.dto.request.CartItem;
import com.kifiya.promotion_quoter.features.promotion.model.Promotion;
import com.kifiya.promotion_quoter.shared.rules.context.CartCalculationContext;
import com.kifiya.promotion_quoter.shared.rules.promo_rules.PromotionRule;
import com.kifiya.promotion_quoter.shared.utils.price.PriceUtil;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class BuyXGetYRule implements PromotionRule {
    @Override
    public void apply(CartCalculationContext context, Promotion promotion) {
        String targetProductId = promotion.getProductId();
        int x = promotion.getX();
        int y = promotion.getY();
        for (CartItem item : context.getItems()) {
            if (targetProductId.equals(item.productId())) {
                int qty = item.quantity();
                int freeItems = (qty / x) * y;
                BigDecimal unitPrice = context.getProducts().get(item.productId()).getPrice();
                BigDecimal paidQty = BigDecimal.valueOf(qty - freeItems);
                BigDecimal newPrice = PriceUtil.toCents(paidQty.multiply(unitPrice));
                BigDecimal discount = BigDecimal.valueOf(freeItems).multiply(unitPrice);
                context.getCurrentPrices().put(item.productId(), newPrice);
                context.addAudit("Applied " + getDescription(promotion) + " to item " + item.productId() + ": free items " + freeItems + ", discount " + discount);
            }
        }
    }

    @Override
    public String getDescription(Promotion promotion) {
        return "Buy " + promotion.getX() + " get " + promotion.getY() + " free for product ID " + promotion.getProductId();
    }
}
