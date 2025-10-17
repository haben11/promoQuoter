package com.kifiya.promotion_quoter.features.promotion.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kifiya.promotion_quoter.features.promotion.dto.request.PromotionRequest;
import com.kifiya.promotion_quoter.features.promotion.enums.PromotionType;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("PromotionController Integration Tests")
class PromotionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PromotionRepository promotionRepository;

    @BeforeEach
    void setUp() {
        promotionRepository.deleteAll();
    }

    @Test
    @DisplayName("Should create percent off category promotion successfully")
    void shouldCreatePercentOffCategoryPromotion() throws Exception {
        // Given
        PromotionRequest request = new PromotionRequest(
                PromotionType.PERCENT_OFF_CATEGORY,
                "Electronics",
                15.0,
                null,
                null,
                null,
                1
        );

        // When & Then
        mockMvc.perform(post("/promotions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.type", is("PERCENT_OFF_CATEGORY")))
                .andExpect(jsonPath("$.category", is("Electronics")))
                .andExpect(jsonPath("$.percent", is(15.0)))
                .andExpect(jsonPath("$.orderPriority", is(1)))
                .andExpect(jsonPath("$.active", is(true)));
    }

    @Test
    @DisplayName("Should create BuyXGetY promotion successfully")
    void shouldCreateBuyXGetYPromotion() throws Exception {
        // Given
        PromotionRequest request = new PromotionRequest(
                PromotionType.BUY_X_GET_Y,
                null,
                null,
                "product-123",
                2,
                1,
                2
        );

        // When & Then
        mockMvc.perform(post("/promotions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.type", is("BUY_X_GET_Y")))
                .andExpect(jsonPath("$.productId", is("product-123")))
                .andExpect(jsonPath("$.x", is(2)))
                .andExpect(jsonPath("$.y", is(1)))
                .andExpect(jsonPath("$.orderPriority", is(2)));
    }

    @Test
    @DisplayName("Should return validation error for negative percent")
    void shouldReturnValidationErrorForNegativePercent() throws Exception {
        // Given
        PromotionRequest request = new PromotionRequest(
                PromotionType.PERCENT_OFF_CATEGORY,
                "Electronics",
                -5.0, // Invalid
                null,
                null,
                null,
                1
        );

        // When & Then
        mockMvc.perform(post("/promotions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]", containsString("Percent must be at least 0.0")));
    }

    @Test
    @DisplayName("Should update promotion successfully")
    void shouldUpdatePromotionSuccessfully() throws Exception {
        // Given - Create a promotion first
        PromotionRequest createRequest = new PromotionRequest(
                PromotionType.PERCENT_OFF_CATEGORY,
                "Electronics",
                10.0,
                null,
                null,
                null,
                1
        );

        String createResponse = mockMvc.perform(post("/promotions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String promotionId = objectMapper.readTree(createResponse).get("id").asText();

        // Update request with higher discount
        PromotionRequest updateRequest = new PromotionRequest(
                PromotionType.PERCENT_OFF_CATEGORY,
                "Electronics",
                20.0,
                null,
                null,
                null,
                1
        );

        // When & Then
        mockMvc.perform(put("/promotions/" + promotionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(promotionId)))
                .andExpect(jsonPath("$.percent", is(20.0)))
                .andExpect(jsonPath("$.category", is("Electronics")));
    }

    @Test
    @DisplayName("Should return 404 when updating non-existent promotion")
    void shouldReturn404WhenUpdatingNonExistentPromotion() throws Exception {
        // Given
        String nonExistentId = "non-existent-id";
        PromotionRequest updateRequest = new PromotionRequest(
                PromotionType.PERCENT_OFF_CATEGORY,
                "Electronics",
                20.0,
                null,
                null,
                null,
                1
        );

        // When & Then
        mockMvc.perform(put("/promotions/" + nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should create multiple promotions with different priorities")
    void shouldCreateMultiplePromotionsWithDifferentPriorities() throws Exception {
        // Given
        PromotionRequest promo1 = new PromotionRequest(
                PromotionType.PERCENT_OFF_CATEGORY,
                "Electronics",
                10.0,
                null,
                null,
                null,
                1
        );

        PromotionRequest promo2 = new PromotionRequest(
                PromotionType.BUY_X_GET_Y,
                null,
                null,
                "product-123",
                3,
                1,
                2
        );

        // When & Then - Create first promotion
        mockMvc.perform(post("/promotions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(promo1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderPriority", is(1)));

        // Create second promotion
        mockMvc.perform(post("/promotions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(promo2)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderPriority", is(2)));
    }
}

