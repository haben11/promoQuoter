package com.kifiya.promotion_quoter.features.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kifiya.promotion_quoter.features.product.dto.request.ProductRequest;
import com.kifiya.promotion_quoter.features.product.repository.ProductRepository;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("ProductController Integration Tests")
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    @Test
    @DisplayName("Should create product successfully")
    void shouldCreateProductSuccessfully() throws Exception {
        // Given
        ProductRequest request = new ProductRequest(
                "MacBook Pro",
                "Electronics",
                new BigDecimal("2499.99"),
                25
        );

        // When & Then
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("MacBook Pro")))
                .andExpect(jsonPath("$.category", is("Electronics")))
                .andExpect(jsonPath("$.price", is(2499.99)))
                .andExpect(jsonPath("$.stock", is(25)));
    }

    @Test
    @DisplayName("Should create product with zero stock")
    void shouldCreateProductWithZeroStock() throws Exception {
        // Given
        ProductRequest request = new ProductRequest(
                "Out of Stock Item",
                "Miscellaneous",
                new BigDecimal("99.99"),
                0
        );

        // When & Then
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.stock", is(0)));
    }

    @Test
    @DisplayName("Should return validation error for blank product name")
    void shouldReturnValidationErrorForBlankName() throws Exception {
        // Given
        ProductRequest request = new ProductRequest(
                "", // Blank name
                "Electronics",
                new BigDecimal("100.00"),
                10
        );

        // When & Then
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]", containsString("Product name is required")));
    }

    @Test
    @DisplayName("Should return validation error for blank category")
    void shouldReturnValidationErrorForBlankCategory() throws Exception {
        // Given
        ProductRequest request = new ProductRequest(
                "Test Product",
                "", // Blank category
                new BigDecimal("100.00"),
                10
        );

        // When & Then
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]", containsString("Product category is required")));
    }

    @Test
    @DisplayName("Should return validation error for null price")
    void shouldReturnValidationErrorForNullPrice() throws Exception {
        // Given
        ProductRequest request = new ProductRequest(
                "Test Product",
                "Electronics",
                null, // Null price
                10
        );

        // When & Then
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]", containsString("Product price is required")));
    }

    @Test
    @DisplayName("Should return validation error for negative stock")
    void shouldReturnValidationErrorForNegativeStock() throws Exception {
        // Given
        ProductRequest request = new ProductRequest(
                "Test Product",
                "Electronics",
                new BigDecimal("100.00"),
                -5 // Negative stock
        );

        // When & Then
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should create products with different categories")
    void shouldCreateProductsWithDifferentCategories() throws Exception {
        // Given
        ProductRequest electronicsProduct = new ProductRequest(
                "Laptop",
                "Electronics",
                new BigDecimal("1000.00"),
                10
        );

        ProductRequest groceryProduct = new ProductRequest(
                "Apple",
                "Groceries",
                new BigDecimal("2.50"),
                100
        );

        // When & Then - Create electronics product
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(electronicsProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.category", is("Electronics")));

        // Create grocery product
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(groceryProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.category", is("Groceries")));
    }

    @Test
    @DisplayName("Should handle decimal prices correctly")
    void shouldHandleDecimalPricesCorrectly() throws Exception {
        // Given
        ProductRequest request = new ProductRequest(
                "Budget Item",
                "Bargain",
                new BigDecimal("0.99"),
                1000
        );

        // When & Then
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.price", is(0.99)));
    }

    @Test
    @DisplayName("Should create product with large stock quantity")
    void shouldCreateProductWithLargeStock() throws Exception {
        // Given
        ProductRequest request = new ProductRequest(
                "High Volume Item",
                "Warehouse",
                new BigDecimal("5.00"),
                100000
        );

        // When & Then
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.stock", is(100000)));
    }
}

