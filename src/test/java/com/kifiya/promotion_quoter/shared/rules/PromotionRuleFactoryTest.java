package com.kifiya.promotion_quoter.shared.rules;

import com.kifiya.promotion_quoter.features.promotion.enums.PromotionType;
import com.kifiya.promotion_quoter.features.promotion.model.Promotion;
import com.kifiya.promotion_quoter.shared.rules.promo_rules.PromotionRule;
import com.kifiya.promotion_quoter.shared.rules.promo_rules.strategy.factory.PromotionRuleFactory;
import com.kifiya.promotion_quoter.shared.rules.promo_rules.strategy.impl.BuyXGetYRule;
import com.kifiya.promotion_quoter.shared.rules.promo_rules.strategy.impl.PercentOffCategoryRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
@DisplayName("Promotion rule factory unit test")
class PromotionRuleFactoryTest {

    @Mock
    private PercentOffCategoryRule percentOffCategoryRule;

    @Mock
    private BuyXGetYRule buyXGetYRule;

    @InjectMocks
    private PromotionRuleFactory promotionRuleFactory;

    private Promotion promotion;

    @BeforeEach
    void setUp() {
        promotion = new Promotion();
        promotion.setId("promo-1");
    }

    @Test
    @DisplayName("Should return PercentOffCategoryRule for PERCENT_OFF_CATEGORY type")
    void shouldReturnPercentOffCategoryRule() {
        // Given
        promotion.setType(PromotionType.PERCENT_OFF_CATEGORY);

        // When
        PromotionRule result = promotionRuleFactory.getRule(promotion);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(PercentOffCategoryRule.class);
    }

    @Test
    @DisplayName("Should return BuyXGetYRule for BUY_X_GET_Y type")
    void shouldReturnBuyXGetYRule() {
        // Given
        promotion.setType(PromotionType.BUY_X_GET_Y);

        // When
        PromotionRule result = promotionRuleFactory.getRule(promotion);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(BuyXGetYRule.class);
    }


    @Test
    @DisplayName("Should return same rule instance for same type (singleton behavior)")
    void shouldReturnSameInstanceForSameType() {
        // Given
        promotion.setType(PromotionType.PERCENT_OFF_CATEGORY);

        // When
        PromotionRule first = promotionRuleFactory.getRule(promotion);
        PromotionRule second = promotionRuleFactory.getRule(promotion);

        // Then
        assertThat(first).isSameAs(second);
    }
}
