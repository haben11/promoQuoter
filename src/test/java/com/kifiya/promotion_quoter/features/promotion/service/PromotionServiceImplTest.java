package com.kifiya.promotion_quoter.features.promotion.service;

import com.kifiya.promotion_quoter.features.promotion.dto.request.PromotionRequest;
import com.kifiya.promotion_quoter.features.promotion.dto.response.PromotionResponse;
import com.kifiya.promotion_quoter.features.promotion.enums.PromotionType;
import com.kifiya.promotion_quoter.features.promotion.exception.PromotionNotFoundException;
import com.kifiya.promotion_quoter.features.promotion.mapper.PromotionMapper;
import com.kifiya.promotion_quoter.features.promotion.model.Promotion;
import com.kifiya.promotion_quoter.features.promotion.repository.PromotionRepository;
import com.kifiya.promotion_quoter.features.promotion.service.impl.PromotionServiceImpl;
import com.kifiya.promotion_quoter.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Promotion Service Unit Tests")
public class PromotionServiceImplTest {
    @Mock
    private PromotionRepository promotionRepository;
    @Mock
    private PromotionMapper mapper;
    @InjectMocks
    private PromotionServiceImpl promotionService;

    private PromotionRequest promotionRequest;
    private Promotion promotion;
    private PromotionResponse promotionResponse;

    @BeforeEach
    void setUp() {

        promotionRequest = TestDataBuilder.buildPercentOffPromotionRequest(
          "Electronics",
          25.0,
              1
        );

        promotion = TestDataBuilder.buildPercentOffPromotion(
          "99e3ac90-63z1-4109-q40r-z5x9b4c2290e",
          "Electronics",
          25.0,
          1
        );

        promotionResponse = new PromotionResponse(
                "99e3ac90-63z1-4109-q40r-z5x9b4c2290e",
                PromotionType.PERCENT_OFF_CATEGORY,
                "Electronics",
                25.0,
                null,
                0,
                0,
                1,
                true
        );
    }

    @Test
    @DisplayName("Should create promotion successfully")
    void shouldCreatePercentOffCategoryPromotionSuccessfully() {
        // Given
        when(mapper.toEntity(promotionRequest)).thenReturn(promotion);
        when(promotionRepository.save(promotion)).thenReturn(promotion);
        when(mapper.toBo(promotion)).thenReturn(promotionResponse);

        // When
        PromotionResponse result = promotionService.createPromotion(promotionRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo("99e3ac90-63z1-4109-q40r-z5x9b4c2290e");
        assertThat(result.type()).isEqualTo(PromotionType.PERCENT_OFF_CATEGORY);
        assertThat(result.category()).isEqualTo("Electronics");
        assertThat(result.percent()).isEqualTo(25.0);

        verify(mapper, times(1)).toEntity(promotionRequest);
        verify(promotionRepository, times(1)).save(promotion);
        verify(mapper, times(1)).toBo(promotion);
    }

    @Test
    @DisplayName("Should create BuyXGetY promotion successfully")
    void shouldCreateBuyXGetYPromotionSuccessfully() {
        // Given
        promotionRequest = TestDataBuilder.buildBuyXGetYPromotionRequest(
                "79w3af90-80z2-6209-n40m-j5x9b4c2290e",
                6,
                2,
                2
        );

        Promotion buyXGetYPromo = new Promotion();
        buyXGetYPromo.setId("15285c89-31f6-43ef-8c67-63d8bd484684");
        buyXGetYPromo.setType(PromotionType.BUY_X_GET_Y);
        buyXGetYPromo.setProductId("79w3af90-80z2-6209-n40m-j5x9b4c2290e");
        buyXGetYPromo.setX(6);
        buyXGetYPromo.setY(2);

        PromotionResponse buyXGetYResponse = new PromotionResponse(
                "15285c89-31f6-43ef-8c67-63d8bd484684",
                PromotionType.BUY_X_GET_Y,
                null,
                null,
                "79w3af90-80z2-6209-n40m-j5x9b4c2290e",
                6,
                2,
                2,
                true
        );

        when(mapper.toEntity(promotionRequest)).thenReturn(buyXGetYPromo);
        when(promotionRepository.save(buyXGetYPromo)).thenReturn(buyXGetYPromo);
        when(mapper.toBo(buyXGetYPromo)).thenReturn(buyXGetYResponse);

        // When
        PromotionResponse result = promotionService.createPromotion(promotionRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.type()).isEqualTo(PromotionType.BUY_X_GET_Y);
        assertThat(result.x()).isEqualTo(3);
        assertThat(result.y()).isEqualTo(1);
        assertThat(result.productId()).isEqualTo("79w3af90-80z2-6209-n40m-j5x9b4c2290e");
    }

    @Test
    @DisplayName("Should update promotion successfully")
    void shouldUpdatePromotionSuccessfully() {
        // Given
        String promotionId = "99e3ac90-63z1-4109-q40r-z5x9b4c2290e";
        PromotionRequest updateRequest = new PromotionRequest(
                PromotionType.PERCENT_OFF_CATEGORY,
                "Electronics",
                20.0,
                null,
                null,
                null,
                1
        );

        Promotion updatedPromotion = new Promotion();
        updatedPromotion.setId(promotionId);
        updatedPromotion.setType(PromotionType.PERCENT_OFF_CATEGORY);
        updatedPromotion.setCategory("Electronics");
        updatedPromotion.setPercent(20.0);

        PromotionResponse updatedResponse = new PromotionResponse(
                promotionId,
                PromotionType.PERCENT_OFF_CATEGORY,
                "Electronics",
                20.0,
                null,
                null,
                null,
                1,
                true
        );

        when(promotionRepository.findById(promotionId)).thenReturn(Optional.of(promotion));
        when(mapper.updateEntity(updateRequest, promotion)).thenReturn(updatedPromotion);
        when(promotionRepository.save(updatedPromotion)).thenReturn(updatedPromotion);
        when(mapper.toBo(updatedPromotion)).thenReturn(updatedResponse);

        // When
        PromotionResponse result = promotionService.updatePromotion(promotionId, updateRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.percent()).isEqualTo(20.0);
        verify(promotionRepository, times(1)).findById(promotionId);
        verify(promotionRepository, times(1)).save(updatedPromotion);
    }

    @Test
    @DisplayName("Should throw PromotionNotFoundException when updating non-existent promotion")
    void shouldThrowExceptionWhenUpdatingNonExistentPromotion() {
        // Given
        String nonExistentId = "49856564-4584594-4594969446-45496";
        when(promotionRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> promotionService.updatePromotion(nonExistentId, promotionRequest))
                .isInstanceOf(PromotionNotFoundException.class);

        verify(promotionRepository, times(1)).findById(nonExistentId);
        verify(promotionRepository, never()).save(any());
    }
}
