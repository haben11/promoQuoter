package com.kifiya.promotion_quoter.features.cart.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kifiya.promotion_quoter.features.order.repository.OrderRepository;
import com.kifiya.promotion_quoter.features.product.model.Product;
import com.kifiya.promotion_quoter.features.product.repository.ProductRepository;
import com.kifiya.promotion_quoter.features.promotion.repository.PromotionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Cart Controller Integration Tests")
public class CartControllerIntegrationTest {

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

    private Product product1;
    private Product product2;
    private Product product3;

    @BeforeEach
    void setUp() {
        //clean database
        orderRepository.deleteAll();
        promotionRepository.deleteAll();
        productRepository.deleteAll();

        //create test products
        product1 = new Product();


    }
}
