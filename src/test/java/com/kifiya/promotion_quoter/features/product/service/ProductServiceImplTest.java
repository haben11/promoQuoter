package com.kifiya.promotion_quoter.features.product.service;

import com.kifiya.promotion_quoter.features.product.dto.request.ProductRequest;
import com.kifiya.promotion_quoter.features.product.dto.response.ProductResponse;
import com.kifiya.promotion_quoter.features.product.mapper.ProductMapper;
import com.kifiya.promotion_quoter.features.product.model.Product;
import com.kifiya.promotion_quoter.features.product.repository.ProductRepository;
import com.kifiya.promotion_quoter.features.product.service.impl.ProductServiceImpl;
import com.kifiya.promotion_quoter.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Product service Unit Tests")
public class ProductServiceImplTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductMapper mapper;
    @InjectMocks
    private ProductServiceImpl productService;

    private ProductRequest productRequest;
    private Product product;
    private ProductResponse productResponse;

    @BeforeEach
    void setUp() {

        productRequest = TestDataBuilder.buildProductRequest(
          "Nivea Men Face Wash",
                "Beauty and Care",
                new BigDecimal("40.00"),
                50
        );

        product = TestDataBuilder.buildProduct(
              "2eb3eb91-1684-4257-bccd-e557b4c2290e",
                "Nivea Men Face Wash",
                "Beauty and Care",
                new BigDecimal("40.00"),
                50
        );

        productResponse = new ProductResponse(
                "2eb3eb91-1684-4257-bccd-e557b4c2290e",
                "Nivea Men Face Wash",
                "Beauty and Care",
                new BigDecimal("40.00"),
                50
        );
    }

    @Test
    @DisplayName("Should create product successfully")
    void shouldCreateProductSuccessfully() {
        // Given
        when(mapper.toEntity(productRequest)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(mapper.toBo(product)).thenReturn(productResponse);

        // When
        ProductResponse result = productService.createProduct(productRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo("2eb3eb91-1684-4257-bccd-e557b4c2290e");
        assertThat(result.name()).isEqualTo("Nivea Men Face Wash");
        assertThat(result.category()).isEqualTo("Beauty and Care");
        assertThat(result.price()).isEqualByComparingTo(new BigDecimal("40.00"));
        assertThat(result.stock()).isEqualTo(50);

        verify(mapper, times(1)).toEntity(productRequest);
        verify(productRepository, times(1)).save(product);
        verify(mapper, times(1)).toBo(product);
    }

    @Test
    @DisplayName("Should create product with zero stock")
    void shouldCreateProductWithZeroStock() {
        // Given
        ProductRequest zeroStockRequest = new ProductRequest(
                "Atomic Habits",
                "Books",
                new BigDecimal("150.00"),
                0
        );

        Product zeroStockProduct = new Product();
        zeroStockProduct.setId("9e03eb91-8490-3497-accd-9re7b4c2290e");
        zeroStockProduct.setName("Atomic Habits");
        zeroStockProduct.setCategory("Books");
        zeroStockProduct.setPrice(new BigDecimal("150.00"));
        zeroStockProduct.setStock(0);

        ProductResponse zeroStockResponse = new ProductResponse(
                "9e03eb91-8490-3497-accd-9re7b4c2290e",
                "Atomic Habits",
                "Books",
                new BigDecimal("150.00"),
                0
        );

        when(mapper.toEntity(zeroStockRequest)).thenReturn(zeroStockProduct);
        when(productRepository.save(zeroStockProduct)).thenReturn(zeroStockProduct);
        when(mapper.toBo(zeroStockProduct)).thenReturn(zeroStockResponse);

        // When
        ProductResponse result = productService.createProduct(zeroStockRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.stock()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should handle product with decimal price correctly")
    void shouldHandleDecimalPriceCorrectly() {
        // Given
        ProductRequest decimalPriceRequest = new ProductRequest(
                "Coffee Beans",
                "Groceries",
                new BigDecimal("12.99"),
                200
        );

        Product decimalPriceProduct = new Product();
        decimalPriceProduct.setPrice(new BigDecimal("12.99"));

        ProductResponse decimalPriceResponse = new ProductResponse(
                "5y03ab70-94a0-0234-a10d-a5e7b4c2290e",
                "Coffee Beans",
                "Groceries",
                new BigDecimal("12.99"),
                200
        );

        when(mapper.toEntity(decimalPriceRequest)).thenReturn(decimalPriceProduct);
        when(productRepository.save(decimalPriceProduct)).thenReturn(decimalPriceProduct);
        when(mapper.toBo(decimalPriceProduct)).thenReturn(decimalPriceResponse);

        // When
        ProductResponse result = productService.createProduct(decimalPriceRequest);

        // Then
        assertThat(result.price()).isEqualByComparingTo(new BigDecimal("12.99"));
    }
}
