package com.kifiya.promotion_quoter.util;

import com.kifiya.promotion_quoter.features.cart.dto.request.CartItem;
import com.kifiya.promotion_quoter.features.cart.dto.request.CartRequestDto;
import com.kifiya.promotion_quoter.features.product.dto.request.ProductRequest;
import com.kifiya.promotion_quoter.features.product.model.Product;
import com.kifiya.promotion_quoter.features.promotion.dto.request.PromotionRequest;
import com.kifiya.promotion_quoter.features.promotion.enums.PromotionType;
import com.kifiya.promotion_quoter.features.promotion.model.Promotion;

import java.math.BigDecimal;
import java.util.List;

public class TestDataBuilder {

    public static Product buildProduct(String id, String name, String category, BigDecimal price, int stock) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setCategory(category);
        product.setPrice(price);
        product.setStock(stock);
        return product;
    }

    public static Product buildDefaultProduct() {
        return buildProduct(
                "test-product-1",
                "Test Product",
                "Electronics",
                new BigDecimal("100.00"),
                50
        );
    }

    public static Product buildElectronicsProduct(String id) {
        return buildProduct(
                id,
                "Electronic Device",
                "Electronics",
                new BigDecimal("500.00"),
                20
        );
    }

    public static ProductRequest buildProductRequest(String name, String category, BigDecimal price, int stock) {
        return new ProductRequest(name, category, price, stock);
    }

    public static ProductRequest buildDefaultProductRequest() {
        return buildProductRequest(
                "Test Product",
                "Electronics",
                new BigDecimal("100.00"),
                50
        );
    }

    public static Promotion buildPercentOffPromotion(String id, String category, double percent, int priority) {
        Promotion promotion = new Promotion();
        promotion.setId(id);
        promotion.setType(PromotionType.PERCENT_OFF_CATEGORY);
        promotion.setCategory(category);
        promotion.setPercent(percent);
        promotion.setOrderPriority(priority);
        promotion.setActive(true);
        return promotion;
    }

    public static Promotion buildBuyXGetYPromotion(String id, String productId, int x, int y, int priority) {
        Promotion promotion = new Promotion();
        promotion.setId(id);
        promotion.setType(PromotionType.BUY_X_GET_Y);
        promotion.setProductId(productId);
        promotion.setX(x);
        promotion.setY(y);
        promotion.setOrderPriority(priority);
        promotion.setActive(true);
        return promotion;
    }

    public static Promotion buildDefaultPercentOffPromotion() {
        return buildPercentOffPromotion(
                "promo-1",
                "Electronics",
                10.0,
                1
        );
    }

    public static PromotionRequest buildPercentOffPromotionRequest(String category, double percent, int priority) {
        return new PromotionRequest(
                PromotionType.PERCENT_OFF_CATEGORY,
                category,
                percent,
                null,
                null,
                null,
                priority
        );
    }

    public static PromotionRequest buildBuyXGetYPromotionRequest(String productId, int x, int y, int priority) {
        return new PromotionRequest(
                PromotionType.BUY_X_GET_Y,
                null,
                null,
                productId,
                x,
                y,
                priority
        );
    }

    public static PromotionRequest buildDefaultPromotionRequest() {
        return buildPercentOffPromotionRequest("Electronics", 10.0, 1);
    }

    // Cart Builders
    public static CartItem buildCartItem(String productId, int quantity) {
        return new CartItem(productId, quantity);
    }

    public static CartRequestDto buildCartRequest(List<CartItem> items, String customerSegment) {
        return new CartRequestDto(items, customerSegment);
    }

    public static CartRequestDto buildSingleItemCart(String productId, int quantity) {
        return buildCartRequest(
                List.of(new CartItem(productId, quantity)),
                "REGULAR"
        );
    }

    public static CartRequestDto buildDefaultCartRequest() {
        return buildCartRequest(
                List.of(
                        new CartItem("product-1", 2),
                        new CartItem("product-2", 1)
                ),
                "REGULAR"
        );
    }

    public static class TestValues {
        public static final String DEFAULT_CATEGORY = "Electronics";
        public static final String GROCERIES_CATEGORY = "Groceries";
        public static final String DEFAULT_CUSTOMER_SEGMENT = "REGULAR";
        public static final String VIP_CUSTOMER_SEGMENT = "VIP";
        public static final BigDecimal DEFAULT_PRICE = new BigDecimal("100.00");
        public static final int DEFAULT_STOCK = 50;
        public static final double DEFAULT_DISCOUNT_PERCENT = 10.0;
        public static final int DEFAULT_PRIORITY = 1;
    }
}
