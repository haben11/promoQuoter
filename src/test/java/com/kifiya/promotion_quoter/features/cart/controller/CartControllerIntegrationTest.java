package com.kifiya.promotion_quoter.features.cart.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kifiya.promotion_quoter.features.cart.dto.request.CartItem;
import com.kifiya.promotion_quoter.features.cart.dto.request.CartRequestDto;
import com.kifiya.promotion_quoter.features.order.repository.OrderRepository;
import com.kifiya.promotion_quoter.features.product.model.Product;
import com.kifiya.promotion_quoter.features.product.repository.ProductRepository;
import com.kifiya.promotion_quoter.features.promotion.enums.PromotionType;
import com.kifiya.promotion_quoter.features.promotion.model.Promotion;
import com.kifiya.promotion_quoter.features.promotion.repository.PromotionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Cart Controller Integration Tests")
class CartControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private OrderRepository orderRepository;

    private Product laptop;
    private Product mouse;
    private Product headphones;

    @BeforeEach
    void setUp() {
        // Clean databases
        orderRepository.deleteAll();
        promotionRepository.deleteAll();
        productRepository.deleteAll();

        // Create test products
        laptop = new Product();
        laptop.setName("Gaming Laptop");
        laptop.setCategory("Electronics");
        laptop.setPrice(new BigDecimal("1500.00"));
        laptop.setStock(10);
        laptop = productRepository.save(laptop);

        mouse = new Product();
        mouse.setName("Wireless Mouse");
        mouse.setCategory("Electronics");
        mouse.setPrice(new BigDecimal("50.00"));
        mouse.setStock(100);
        mouse = productRepository.save(mouse);

        headphones = new Product();
        headphones.setName("Noise Cancelling Headphones");
        headphones.setCategory("Electronics");
        headphones.setPrice(new BigDecimal("200.00"));
        headphones.setStock(50);
        headphones = productRepository.save(headphones);
    }

    @Test
    @DisplayName("Should calculate quote without promotions")
    void shouldCalculateQuoteWithoutPromotions() throws Exception {
        // Given
        CartRequestDto request = new CartRequestDto(
                List.of(
                        new CartItem(laptop.getId(), 1),
                        new CartItem(mouse.getId(), 2)
                ),
                "REGULAR"
        );

        // When & Then
        mockMvc.perform(post("/cart/quote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemPrices",notNullValue()))
                .andExpect(jsonPath("$.totalPrice", is(1600.00)))
                .andExpect(jsonPath("$.appliedPromotions").isEmpty());
    }

    @Test
    @DisplayName("Should calculate quote with percent off category promotion")
    void shouldCalculateQuoteWithPercentOffPromotion() throws Exception {
        // Given - 10% off Electronics
        Promotion promotion = new Promotion();
        promotion.setType(PromotionType.PERCENT_OFF_CATEGORY);
        promotion.setCategory("Electronics");
        promotion.setPercent(10.0);
        promotion.setOrderPriority(1);
        promotion.setActive(true);
        promotionRepository.save(promotion);

        CartRequestDto request = new CartRequestDto(
                List.of(new CartItem(laptop.getId(), 1)),
                "REGULAR"
        );

        // When & Then
        mockMvc.perform(post("/cart/quote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemPrices",notNullValue()))
                .andExpect(jsonPath("$.totalPrice", is(1350.00)))
                .andExpect(jsonPath("$.appliedPromotions", hasSize(greaterThan(0))));
    }

    @Test
    @DisplayName("Should calculate quote with BuyXGetY promotion")
    void shouldCalculateQuoteWithBuyXGetYPromotion() throws Exception {
        // Given - Buy 3 Get 1 Free for mouse
        Promotion promotion = new Promotion();
        promotion.setType(PromotionType.BUY_X_GET_Y);
        promotion.setProductId(mouse.getId());
        promotion.setX(3);
        promotion.setY(1);
        promotion.setOrderPriority(1);
        promotion.setActive(true);
        promotionRepository.save(promotion);

        CartRequestDto request = new CartRequestDto(
                List.of(new CartItem(mouse.getId(), 4)),
                "REGULAR"
        );

        // When & Then
        mockMvc.perform(post("/cart/quote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemPrices",notNullValue()))
                .andExpect(jsonPath("$.totalPrice", is(150.00)))
                .andExpect(jsonPath("$.appliedPromotions", hasSize(greaterThan(0))));
    }

    @Test
    @DisplayName("Should apply multiple promotions in correct order")
    void shouldApplyMultiplePromotionsInOrder() throws Exception {
        // Given - Two promotions with different priorities
        Promotion promo1 = new Promotion();
        promo1.setType(PromotionType.PERCENT_OFF_CATEGORY);
        promo1.setCategory("Electronics");
        promo1.setPercent(10.0);
        promo1.setOrderPriority(1);
        promo1.setActive(true);
        promotionRepository.save(promo1);

        Promotion promo2 = new Promotion();
        promo2.setType(PromotionType.BUY_X_GET_Y);
        promo2.setProductId(mouse.getId());
        promo2.setX(2);
        promo2.setY(1);
        promo2.setOrderPriority(2);
        promo2.setActive(true);
        promotionRepository.save(promo2);

        CartRequestDto request = new CartRequestDto(
                List.of(new CartItem(mouse.getId(), 3)),
                "REGULAR"
        );

        // When & Then
        mockMvc.perform(post("/cart/quote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.appliedPromotions", hasSize(greaterThan(0))));
    }

    @Test
    @DisplayName("Should confirm order and reserve stock")
    void shouldConfirmOrderAndReserveStock() throws Exception {
        // Given
        int initialStock = laptop.getStock();
        CartRequestDto request = new CartRequestDto(
                List.of(new CartItem(laptop.getId(), 2)),
                "REGULAR"
        );

        // When & Then
        mockMvc.perform(post("/cart/confirm")
                        .header("Idempotency-Key", "test-key-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.finalPrice", is(3000.00)))
                .andExpect(jsonPath("$.orderId", notNullValue()));

        // Verify stock was reduced
        Product updatedLaptop = productRepository.findById(laptop.getId()).orElseThrow();
        org.assertj.core.api.Assertions.assertThat(updatedLaptop.getStock()).isEqualTo(initialStock - 2);
    }

    @Test
    @DisplayName("Should handle idempotent requests correctly")
    void shouldHandleIdempotentRequests() throws Exception {
        // Given
        String idempotencyKey = "duplicate-key-456";
        CartRequestDto request = new CartRequestDto(
                List.of(new CartItem(laptop.getId(), 1)),
                "REGULAR"
        );

        // When - First request
        String firstResponse = mockMvc.perform(post("/cart/confirm")
                        .header("Idempotency-Key", idempotencyKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String firstOrderId = objectMapper.readTree(firstResponse).get("orderId").asText();
        int stockAfterFirst = productRepository.findById(laptop.getId()).orElseThrow().getStock();

        // When - Second request with same idempotency key
        String secondResponse = mockMvc.perform(post("/cart/confirm")
                        .header("Idempotency-Key", idempotencyKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String secondOrderId = objectMapper.readTree(secondResponse).get("orderId").asText();
        int stockAfterSecond = productRepository.findById(laptop.getId()).orElseThrow().getStock();

        // Then - Should return same order and not reduce stock again
        org.assertj.core.api.Assertions.assertThat(secondOrderId).isEqualTo(firstOrderId);
        org.assertj.core.api.Assertions.assertThat(stockAfterSecond).isEqualTo(stockAfterFirst);
    }

    @Test
    @DisplayName("Should return error for insufficient stock")
    void shouldReturnErrorForInsufficientStock() throws Exception {
        // Given
        CartRequestDto request = new CartRequestDto(
                List.of(new CartItem(laptop.getId(), 999)), // More than available
                "REGULAR"
        );

        // When & Then
        mockMvc.perform(post("/cart/confirm")
                        .header("Idempotency-Key", "test-key-789")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorMessage", containsString("Insufficient stock")));
    }

    @Test
    @DisplayName("Should return error for non-existent product")
    void shouldReturnErrorForNonExistentProduct() throws Exception {
        // Given
        CartRequestDto request = new CartRequestDto(
                List.of(new CartItem("non-existent-product", 1)),
                "REGULAR"
        );

        // When & Then
        mockMvc.perform(post("/cart/quote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage", containsString("Product not found")));
    }

    @Test
    @DisplayName("Should return validation error for empty cart")
    void shouldReturnValidationErrorForEmptyCart() throws Exception {
        // Given
        CartRequestDto request = new CartRequestDto(
                List.of(), // Empty cart
                "REGULAR"
        );

        // When & Then
        mockMvc.perform(post("/cart/quote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return validation error for invalid quantity")
    void shouldReturnValidationErrorForInvalidQuantity() throws Exception {
        // Given
        CartRequestDto request = new CartRequestDto(
                List.of(new CartItem(laptop.getId(), 0)),
                "REGULAR"
        );

        // When & Then
        mockMvc.perform(post("/cart/quote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage", containsString("Quantity must be at least 1")));
    }

    @Test
    @DisplayName("Should handle complex cart with multiple items and promotions")
    void shouldHandleComplexCart() throws Exception {
        // Given - Setup complex scenario
        Promotion categoryDiscount = new Promotion();
        categoryDiscount.setType(PromotionType.PERCENT_OFF_CATEGORY);
        categoryDiscount.setCategory("Electronics");
        categoryDiscount.setPercent(15.0);
        categoryDiscount.setOrderPriority(1);
        categoryDiscount.setActive(true);
        promotionRepository.save(categoryDiscount);

        CartRequestDto request = new CartRequestDto(
                List.of(
                        new CartItem(laptop.getId(), 1),
                        new CartItem(mouse.getId(), 3),
                        new CartItem(headphones.getId(), 2)
                ),
                "REGULAR"
        );

        // When & Then
        mockMvc.perform(post("/cart/quote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemPrices", aMapWithSize(3)))
                .andExpect(jsonPath("$.totalPrice", is(1742.50)));
    }

    @Test
    @DisplayName("Should confirm order without idempotency key")
    void shouldConfirmOrderWithoutIdempotencyKey() throws Exception {
        // Given
        CartRequestDto request = new CartRequestDto(
                List.of(new CartItem(mouse.getId(), 1)),
                "REGULAR"
        );

        // When & Then
        mockMvc.perform(post("/cart/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId", notNullValue()))
                .andExpect(jsonPath("$.finalPrice", notNullValue()));
    }
}
