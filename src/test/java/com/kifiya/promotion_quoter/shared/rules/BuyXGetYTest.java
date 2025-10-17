package com.kifiya.promotion_quoter.shared.rules;

import com.kifiya.promotion_quoter.features.cart.dto.request.CartItem;
import com.kifiya.promotion_quoter.features.product.model.Product;
import com.kifiya.promotion_quoter.features.promotion.model.Promotion;
import com.kifiya.promotion_quoter.shared.rules.context.CartCalculationContext;
import com.kifiya.promotion_quoter.shared.rules.promo_rules.strategy.impl.BuyXGetYRule;
import com.kifiya.promotion_quoter.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("BuyXGetY promotion rule unit test")
class BuyXGetYTest {

    private BuyXGetYRule buyXGetYRule;
    private CartCalculationContext context;
    private Promotion promotion;

    @BeforeEach
    void setUp() {
        buyXGetYRule = new BuyXGetYRule();
        context = new CartCalculationContext();

        promotion = TestDataBuilder.buildBuyXGetYPromotion("4657363-3857-45u4-eyr74583943", "84543ee453-5963-rt56-39548584585", 3, 1, 1);
    }

    @Test
    @DisplayName("Should apply Buy 3 Get 1 Free promotion correctly")
    void shouldApplyBuy3Get1FreeCorrectly() {
        // Given
        Product product = new Product();
        product.setId("84543ee453-5963-rt56-39548584585");
        product.setName("Soda Can");
        product.setPrice(new BigDecimal("2.00"));

        CartItem cartItem = new CartItem("84543ee453-5963-rt56-39548584585", 4);

        Map<String, Product> products = new HashMap<>();
        products.put("84543ee453-5963-rt56-39548584585", product);

        Map<String, BigDecimal> currentPrices = new HashMap<>();
        currentPrices.put("84543ee453-5963-rt56-39548584585", new BigDecimal("8.00"));

        context.setItems(List.of(cartItem));
        context.setProducts(products);
        context.setCurrentPrices(currentPrices);

        // When
        buyXGetYRule.apply(context, promotion);

        // Then
        BigDecimal expectedPrice = new BigDecimal("6.00");
        assertThat(context.getCurrentPrices().get("84543ee453-5963-rt56-39548584585")).isEqualByComparingTo(expectedPrice);
        assertThat(context.getAuditTrail()).hasSize(1);
        assertThat(context.getAuditTrail().get(0)).contains("free items 1");
    }

    @Test
    @DisplayName("Should handle multiple sets of Buy X Get Y")
    void shouldHandleMultipleSets() {
        // Given:
        promotion.setX(2);
        promotion.setY(1);

        Product product = new Product();
        product.setId("84543ee453-5963-rt56-39548584590");
        product.setPrice(new BigDecimal("5.00"));

        CartItem cartItem = new CartItem("84543ee453-5963-rt56-39548584590", 6);

        Map<String, Product> products = new HashMap<>();
        products.put("84543ee453-5963-rt56-39548584590", product);

        Map<String, BigDecimal> currentPrices = new HashMap<>();
        currentPrices.put("84543ee453-5963-rt56-39548584590", new BigDecimal("30.00"));

        context.setItems(List.of(cartItem));
        context.setProducts(products);
        context.setCurrentPrices(currentPrices);

        // When
        buyXGetYRule.apply(context, promotion);

        // Then
        BigDecimal expectedPrice = new BigDecimal("30.00");
        assertThat(context.getCurrentPrices().get("84543ee453-5963-rt56-39548584590")).isEqualByComparingTo(expectedPrice);
    }

    @Test
    @DisplayName("Should not apply promotion when quantity is less than X")
    void shouldNotApplyWhenQuantityLessThanX() {
        // Given: Buy 3 Get 1, customer only buys 2
        Product product = new Product();
        product.setId("049584334-ty37-934r-uw742484949244");
        product.setPrice(new BigDecimal("10.00"));

        CartItem cartItem = new CartItem("049584334-ty37-934r-uw742484949244", 2);

        Map<String, Product> products = new HashMap<>();
        products.put("049584334-ty37-934r-uw742484949244", product);

        Map<String, BigDecimal> currentPrices = new HashMap<>();
        currentPrices.put("049584334-ty37-934r-uw742484949244", new BigDecimal("20.00"));

        context.setItems(List.of(cartItem));
        context.setProducts(products);
        context.setCurrentPrices(currentPrices);

        // When
        buyXGetYRule.apply(context, promotion);

        // Then
        // Still pay full price
        assertThat(context.getCurrentPrices().get("049584334-ty37-934r-uw742484949244")).isEqualByComparingTo(new BigDecimal("20.00"));
    }

    @Test
    @DisplayName("Should only apply to target product")
    void shouldOnlyApplyToTargetProduct() {
        // Given
        Product product1 = new Product();
        product1.setId("d5e8b4e7-07fa-47df-8952-fd1f0f7b87bb");
        product1.setPrice(new BigDecimal("5.00"));

        Product product2 = new Product();
        product2.setId("b3ae23ea-8b36-46b9-b6bd-0ab30a202e7f");
        product2.setPrice(new BigDecimal("10.00"));

        CartItem cartItem1 = new CartItem("d5e8b4e7-07fa-47df-8952-fd1f0f7b87bb", 4);
        CartItem cartItem2 = new CartItem("b3ae23ea-8b36-46b9-b6bd-0ab30a202e7f", 4);

        Map<String, Product> products = new HashMap<>();
        products.put("d5e8b4e7-07fa-47df-8952-fd1f0f7b87bb", product1);
        products.put("b3ae23ea-8b36-46b9-b6bd-0ab30a202e7f", product2);

        Map<String, BigDecimal> currentPrices = new HashMap<>();
        currentPrices.put("d5e8b4e7-07fa-47df-8952-fd1f0f7b87bb", new BigDecimal("20.00"));
        currentPrices.put("b3ae23ea-8b36-46b9-b6bd-0ab30a202e7f", new BigDecimal("40.00"));

        context.setItems(List.of(cartItem1, cartItem2));
        context.setProducts(products);
        context.setCurrentPrices(currentPrices);

        // When
        buyXGetYRule.apply(context, promotion);

        // Then
        assertThat(context.getCurrentPrices().get("d5e8b4e7-07fa-47df-8952-fd1f0f7b87bb")).isEqualByComparingTo(new BigDecimal("20.00"));
        //keeps original price (no promotion)
        assertThat(context.getCurrentPrices().get("b3ae23ea-8b36-46b9-b6bd-0ab30a202e7f")).isEqualByComparingTo(new BigDecimal("40.00"));
    }

    @Test
    @DisplayName("Should generate correct description")
    void shouldGenerateCorrectDescription() {
        // When
        String description = buyXGetYRule.getDescription(promotion);
        // Then
        assertThat(description).isEqualTo("Buy 3 get 1 free for product ID 84543ee453-5963-rt56-39548584585");
    }
}
