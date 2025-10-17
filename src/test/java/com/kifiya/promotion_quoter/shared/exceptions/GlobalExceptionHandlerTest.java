package com.kifiya.promotion_quoter.shared.exceptions;

import com.kifiya.promotion_quoter.shared.exceptions.base.BaseException;
import com.kifiya.promotion_quoter.shared.exceptions.base.ResourceNotFoundException;
import com.kifiya.promotion_quoter.shared.exceptions.dto.ErrorResponse;
import com.kifiya.promotion_quoter.shared.exceptions.global.GlobalException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Global Exception Handler Unit Test")
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalException globalExceptionHandler;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        when(request.getRequestURI()).thenReturn("/test/endpoint");
    }

    @Test
    @DisplayName("Should handle BaseException and return BAD_REQUEST")
    void shouldHandleBaseException() {
        // Given
        BaseException exception = new BaseException("Custom error message");

        // When
        ResponseEntity<?> response = globalExceptionHandler.handleBadRequestException(exception, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);

        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertThat(errorResponse.errorMessage()).isEqualTo("Custom error message");
        assertThat(errorResponse.path()).isEqualTo("/test/endpoint");
        assertThat(errorResponse.status()).isEqualTo(400);
    }

    @Test
    @DisplayName("Should handle ResourceNotFoundException")
    void shouldHandleResourceNotFoundException() {
        // Given
        ResourceNotFoundException exception = new ResourceNotFoundException("Resource not found");

        // When
        ResponseEntity<?> response = globalExceptionHandler.handleResourceNotFoundException(exception, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);

        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertThat(errorResponse.errorMessage()).isEqualTo("Resource not found");
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException with field errors")
    void shouldHandleMethodArgumentNotValidException() {
        // Given
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError = new FieldError("product", "name", "must not be blank");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        when(bindingResult.getGlobalErrors()).thenReturn(List.of());
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(exception.getMessage()).thenReturn("Validation failed");

        // When
        ResponseEntity<?> response = globalExceptionHandler.handleValidationErrors(exception, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);

        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertThat(errorResponse.errors()).isNotNull();
        assertThat(errorResponse.errors()).hasSize(1);
        assertThat(errorResponse.errors().get(0)).contains("name");
        assertThat(errorResponse.errors().get(0)).contains("must not be blank");
    }

    @Test
    @DisplayName("Should handle ConstraintViolationException")
    void shouldHandleConstraintViolationException() {
        // Given
        Set<ConstraintViolation<?>> violations = new HashSet<>();
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        when(violation.getPropertyPath()).thenReturn(mock(jakarta.validation.Path.class));
        when(violation.getMessage()).thenReturn("must be positive");
        violations.add(violation);

        ConstraintViolationException exception = new ConstraintViolationException("Constraint violation", violations);

        // When
        ResponseEntity<?> response = globalExceptionHandler.handleConstraintViolationException(exception, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);

        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertThat(errorResponse.errors()).isNotNull();
        assertThat(errorResponse.errors()).isNotEmpty();
    }

    @Test
    @DisplayName("Should handle generic Exception and return INTERNAL_SERVER_ERROR")
    void shouldHandleGenericException() {
        // Given
        Exception exception = new RuntimeException("Unexpected error");

        // When
        ResponseEntity<?> response = globalExceptionHandler.handleAll(exception, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);

        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertThat(errorResponse.errorMessage()).isEqualTo("Unexpected error");
        assertThat(errorResponse.status()).isEqualTo(500);
    }

    @Test
    @DisplayName("Should include timestamp in error response")
    void shouldIncludeTimestampInErrorResponse() {
        // Given
        BaseException exception = new BaseException("Test error");

        // When
        ResponseEntity<?> response = globalExceptionHandler.handleBadRequestException(exception, request);

        // Then
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertThat(errorResponse.timeStamp()).isNotNull();
    }

    @Test
    @DisplayName("Should include request path in error response")
    void shouldIncludeRequestPathInErrorResponse() {
        // Given
        when(request.getRequestURI()).thenReturn("/api/products/123");
        BaseException exception = new BaseException("Test error");

        // When
        ResponseEntity<?> response = globalExceptionHandler.handleBadRequestException(exception, request);

        // Then
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertThat(errorResponse.path()).isEqualTo("/api/products/123");
    }
}
