package com.kifiya.promotion_quoter.shared.rules.promo_rules.decorator;

import com.kifiya.promotion_quoter.features.promotion.model.Promotion;
import com.kifiya.promotion_quoter.shared.rules.context.CartCalculationContext;
import com.kifiya.promotion_quoter.shared.rules.promo_rules.PromotionRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

public class AuditDecorator implements PromotionRule {

    private static final Logger log = LoggerFactory.getLogger(AuditDecorator.class);
    private final PromotionRule delegate;

    public AuditDecorator(PromotionRule delegate) {
        this.delegate = delegate;
    }

    @Override
    public void apply(CartCalculationContext context, Promotion promotion) {
        BigDecimal totalBefore = context.getCurrentPrices().values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        delegate.apply(context, promotion);
        BigDecimal totalAfter = context.getCurrentPrices().values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        String audit = "Applied " + delegate.getDescription(promotion) + ": total changed from " + totalBefore + " to " + totalAfter;
        context.addAudit(audit);
        log.debug(audit);
    }

    @Override
    public String getDescription(Promotion promotion) {
        return delegate.getDescription(promotion);
    }
}
