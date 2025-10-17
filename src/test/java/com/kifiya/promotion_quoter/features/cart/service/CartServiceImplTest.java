package com.kifiya.promotion_quoter.features.cart.service;

import com.kifiya.promotion_quoter.features.cart.dto.request.CartRequestDto;
import com.kifiya.promotion_quoter.features.cart.dto.response.CartConfirmResponse;
import com.kifiya.promotion_quoter.features.cart.dto.response.CartQuoteResponse;
import com.kifiya.promotion_quoter.features.cart.service.impl.CartServiceImpl;
import com.kifiya.promotion_quoter.features.order.model.OrderReservation;
import com.kifiya.promotion_quoter.features.order.repository.OrderRepository;
import com.kifiya.promotion_quoter.features.product.model.Product;
import com.kifiya.promotion_quoter.features.product.repository.ProductRepository;
import com.kifiya.promotion_quoter.features.promotion.model.Promotion;
import com.kifiya.promotion_quoter.features.promotion.repository.PromotionRepository;
import com.kifiya.promotion_quoter.shared.rules.promo_rules.PromotionRule;
import com.kifiya.promotion_quoter.shared.rules.promo_rules.strategy.factory.PromotionRuleFactory;
import com.kifiya.promotion_quoter.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Cart service Unit Tests")
class CartServiceImplTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private PromotionRepository promotionRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private PromotionRuleFactory ruleFactory;
    @Mock
    private PromotionRule promotionRule;
    @InjectMocks
    private CartServiceImpl cartService;

    private CartRequestDto cartRequestDto;
    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        //setup products
        product1 = TestDataBuilder.buildProduct(
                "product-1",
                "Laptop",
                "Electronics",
                new BigDecimal(1500),
                100
        );

        product2 = TestDataBuilder.buildProduct(
                "product-2",
                "T-shirt",
                "Clothing",
                new BigDecimal(100),
                30
        );

        //setup cart request
        cartRequestDto = TestDataBuilder.buildCartRequest(
                List.of(
                        TestDataBuilder.buildCartItem("product-1", 2),
                        TestDataBuilder.buildCartItem("product-2", 3)
                ),
                "REGULAR"
        );
    }

    @Test
    @DisplayName("Should calculate quote without promotions")
    void shouldCalculateQuoteWithoutPromotions() {

        //Given
        when(productRepository.findById("product-1")).thenReturn(Optional.of(product1));
        when(productRepository.findById("product-2")).thenReturn(Optional.of(product2));
        when(promotionRepository.findAllByActiveTrue()).thenReturn(List.of());

        //when
        CartQuoteResponse response = cartService.quote(cartRequestDto);

        //Then
        assertThat(response).isNotNull();
        assertThat(response.totalPrice()).isNotNull();

        assertThat(response.totalPrice()).isEqualByComparingTo(new BigDecimal("3300.00"));
        assertThat(response.itemPrices()).hasSize(2);
        assertThat(response.appliedPromotions()).hasSize(0);

        verify(productRepository, times(1)).findById("product-1");
        verify(productRepository, times(1)).findById("product-2");
        verify(promotionRepository, times(1)).findAllByActiveTrue();
    }

    @Test
    @DisplayName("Should calculate quote with promotions applied")
    void shouldCalculateQuoteWithPromotions() {

        //Given
        Promotion promotion = TestDataBuilder.buildPercentOffPromotion(
                "promo-123",
                "Electronics",
                35,
                1
        );

        when(productRepository.findById("product-1")).thenReturn(Optional.of(product1));
        when(productRepository.findById("product-2")).thenReturn(Optional.of(product2));
        when(promotionRepository.findAllByActiveTrue()).thenReturn(List.of(promotion));
        when(ruleFactory.getRule(promotion)).thenReturn(promotionRule);
        doNothing().when(promotionRule).apply(any(), any());

        //when
        CartQuoteResponse response = cartService.quote(cartRequestDto);

        //Then
        assertThat(response).isNotNull();
        verify(promotionRule, times(1)).apply(any(), eq(promotion));
    }

    @Test
    @DisplayName("Should confirm order and reserve stock")
    void shouldConfirmOrderAndReserveStock() {

        // Given
        String idempotencyKey = "idempotency-key-123";

        when(orderRepository.findByIdempotencyKey(idempotencyKey)).thenReturn(Optional.empty());
        when(productRepository.findById("product-1")).thenReturn(Optional.of(product1));
        when(productRepository.findById("product-2")).thenReturn(Optional.of(product2));
        when(orderRepository.save(any(OrderReservation.class))).thenAnswer(invocation -> {
            OrderReservation order = invocation.getArgument(0);
            order.setId("order-123");
            return order;
        });

        // When
        CartConfirmResponse response = cartService.confirm(cartRequestDto, idempotencyKey);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.orderId()).isEqualTo("order-123");
        assertThat(response.finalPrice()).isNotNull();

        // Verify stock was reduced
        assertThat(product1.getStock()).isEqualTo(98);
        assertThat(product2.getStock()).isEqualTo(27);

        verify(productRepository, times(2)).save(any(Product.class));
        verify(orderRepository, times(1)).save(any(OrderReservation.class));
    }
}