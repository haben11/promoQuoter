package com.kifiya.promotion_quoter.shared.rules;

import com.kifiya.promotion_quoter.features.cart.dto.request.CartItem;
import com.kifiya.promotion_quoter.features.product.model.Product;
import com.kifiya.promotion_quoter.features.promotion.enums.PromotionType;
import com.kifiya.promotion_quoter.features.promotion.model.Promotion;
import com.kifiya.promotion_quoter.shared.rules.context.CartCalculationContext;
import com.kifiya.promotion_quoter.shared.rules.promo_rules.strategy.impl.PercentOffCategoryRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PercentOffCategory promotion rule unit test")
class PercentOffCategoryTest {

    private PercentOffCategoryRule percentOffCategoryRule;
    private CartCalculationContext context;
    private Promotion promotion;

    @BeforeEach
    void setUp() {
        percentOffCategoryRule = new PercentOffCategoryRule();
        context = new CartCalculationContext();

        // Setup promotion: 10% off Electronics
        promotion = new Promotion();
        promotion.setId("6854838fr84-07fa-47df-8952-fd1f0f7b87bb");
        promotion.setType(PromotionType.PERCENT_OFF_CATEGORY);
        promotion.setCategory("Electronics");
        promotion.setPercent(10.0);
        promotion.setActive(true);
    }

    @Test
    @DisplayName("Should apply 10% discount on Electronics category")
    void shouldApply10PercentDiscountOnElectronics() {
        // Given
        Product product = new Product();
        product.setId("8457485394-3483835-38383-33583733");
        product.setName("Laptop");
        product.setCategory("Electronics");
        product.setPrice(new BigDecimal("1000.00"));

        CartItem cartItem = new CartItem("8457485394-3483835-38383-33583733", 1);

        Map<String, Product> products = new HashMap<>();
        products.put("8457485394-3483835-38383-33583733", product);

        Map<String, BigDecimal> currentPrices = new HashMap<>();
        currentPrices.put("8457485394-3483835-38383-33583733", new BigDecimal("1000.00"));

        context.setItems(List.of(cartItem));
        context.setProducts(products);
        context.setCurrentPrices(currentPrices);

        // When
        percentOffCategoryRule.apply(context, promotion);

        // Then
        BigDecimal expectedPrice = new BigDecimal("900.00");
        assertThat(context.getCurrentPrices().get("8457485394-3483835-38383-33583733")).isEqualByComparingTo(expectedPrice);
        assertThat(context.getAuditTrail()).hasSize(1);
        assertThat(context.getAuditTrail().get(0)).contains("10.0% off on category Electronics");
    }

    @Test
    @DisplayName("Should apply 25% discount correctly")
    void shouldApply25PercentDiscount() {
        // Given
        promotion.setPercent(25.0);

        Product product = new Product();
        product.setId("product-1");
        product.setCategory("Electronics");
        product.setPrice(new BigDecimal("200.00"));

        CartItem cartItem = new CartItem("product-1", 2);

        Map<String, Product> products = new HashMap<>();
        products.put("product-1", product);

        Map<String, BigDecimal> currentPrices = new HashMap<>();
        currentPrices.put("product-1", new BigDecimal("40000")); // $200 * 2 = $400 = 40000 cents

        context.setItems(List.of(cartItem));
        context.setProducts(products);
        context.setCurrentPrices(currentPrices);

        // When
        percentOffCategoryRule.apply(context, promotion);

        // Then
        BigDecimal expectedPrice = new BigDecimal("30000");
        assertThat(context.getCurrentPrices().get("product-1")).isEqualByComparingTo(expectedPrice);
    }

    @Test
    @DisplayName("Should not apply discount to different category")
    void shouldNotApplyDiscountToDifferentCategory() {
        // Given
        Product product = new Product();
        product.setId("product-1");
        product.setCategory("Groceries");
        product.setPrice(new BigDecimal("50.00"));

        CartItem cartItem = new CartItem("product-1", 1);

        Map<String, Product> products = new HashMap<>();
        products.put("product-1", product);

        Map<String, BigDecimal> currentPrices = new HashMap<>();
        BigDecimal originalPrice = new BigDecimal("5000");
        currentPrices.put("product-1", originalPrice);

        context.setItems(List.of(cartItem));
        context.setProducts(products);
        context.setCurrentPrices(currentPrices);

        // When
        percentOffCategoryRule.apply(context, promotion);

        // Then
        assertThat(context.getCurrentPrices().get("product-1")).isEqualByComparingTo(originalPrice);
        assertThat(context.getAuditTrail()).isEmpty();
    }

    @Test
    @DisplayName("Should apply discount only to matching category in mixed cart")
    void shouldApplyDiscountOnlyToMatchingCategory() {
        // Given
        Product electronicsProduct = new Product();
        electronicsProduct.setId("product-1");
        electronicsProduct.setCategory("Electronics");
        electronicsProduct.setPrice(new BigDecimal("100.00"));

        Product groceryProduct = new Product();
        groceryProduct.setId("product-2");
        groceryProduct.setCategory("Groceries");
        groceryProduct.setPrice(new BigDecimal("50.00"));

        CartItem item1 = new CartItem("product-1", 1);
        CartItem item2 = new CartItem("product-2", 1);

        Map<String, Product> products = new HashMap<>();
        products.put("product-1", electronicsProduct);
        products.put("product-2", groceryProduct);

        Map<String, BigDecimal> currentPrices = new HashMap<>();
        currentPrices.put("product-1", new BigDecimal("10000")); // $100
        currentPrices.put("product-2", new BigDecimal("5000"));  // $50

        context.setItems(List.of(item1, item2));
        context.setProducts(products);
        context.setCurrentPrices(currentPrices);

        // When
        percentOffCategoryRule.apply(context, promotion);

        // Then
        // Electronics: should apply 10% off
        assertThat(context.getCurrentPrices().get("product-1")).isEqualByComparingTo(new BigDecimal("9000"));
        // Groceries: unchanged
        assertThat(context.getCurrentPrices().get("product-2")).isEqualByComparingTo(new BigDecimal("5000"));
        assertThat(context.getAuditTrail()).hasSize(1);
    }

    @Test
    @DisplayName("Should handle 100% discount (free)")
    void shouldHandle100PercentDiscount() {
        // Given
        promotion.setPercent(100.0);

        Product product = new Product();
        product.setId("product-1");
        product.setCategory("Electronics");
        product.setPrice(new BigDecimal("250.00"));

        CartItem cartItem = new CartItem("product-1", 1);

        Map<String, Product> products = new HashMap<>();
        products.put("product-1", product);

        Map<String, BigDecimal> currentPrices = new HashMap<>();
        currentPrices.put("product-1", new BigDecimal("25000"));

        context.setItems(List.of(cartItem));
        context.setProducts(products);
        context.setCurrentPrices(currentPrices);

        // When
        percentOffCategoryRule.apply(context, promotion);

        // Then
        // 100% off = free
        assertThat(context.getCurrentPrices().get("product-1")).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should prevent negative prices")
    void shouldPreventNegativePrices() {
        // Given - This shouldn't happen but test edge case
        promotion.setPercent(150.0);

        Product product = new Product();
        product.setId("product-1");
        product.setCategory("Electronics");
        product.setPrice(new BigDecimal("100.00"));

        CartItem cartItem = new CartItem("product-1", 1);

        Map<String, Product> products = new HashMap<>();
        products.put("product-1", product);

        Map<String, BigDecimal> currentPrices = new HashMap<>();
        currentPrices.put("product-1", new BigDecimal("10000"));

        context.setItems(List.of(cartItem));
        context.setProducts(products);
        context.setCurrentPrices(currentPrices);

        // When
        percentOffCategoryRule.apply(context, promotion);

        // Then
        // Should be clamped to zero, not negative
        assertThat(context.getCurrentPrices().get("product-1")).isGreaterThanOrEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should generate correct description")
    void shouldGenerateCorrectDescription() {
        // When
        String description = percentOffCategoryRule.getDescription(promotion);

        // Then
        assertThat(description).isEqualTo("10.0% off on category Electronics");
    }
}
