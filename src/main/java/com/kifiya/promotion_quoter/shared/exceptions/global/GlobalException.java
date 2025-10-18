package com.kifiya.promotion_quoter.shared.exceptions.global;

import com.kifiya.promotion_quoter.shared.exceptions.base.BaseException;
import com.kifiya.promotion_quoter.shared.exceptions.base.InsufficientStockException;
import com.kifiya.promotion_quoter.shared.exceptions.base.ResourceNotFoundException;
import com.kifiya.promotion_quoter.shared.exceptions.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException ex, HttpServletRequest request) {
        List<String> errors = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.toList());

        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request, errors);

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {

        List<String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> String.format("%s: %s", error.getField(), error.getDefaultMessage()))
                .toList();

        List<String> globalErrors = ex.getBindingResult()
                .getGlobalErrors()
                .stream()
                .map(error -> String.format("%s: %s", error.getObjectName(), error.getDefaultMessage()))
                .toList();

        List<String> allErrors = new ArrayList<>();
        allErrors.addAll(fieldErrors);
        allErrors.addAll(globalErrors);

        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request, allErrors);
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<?> handleBadRequestException(BaseException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request, null);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request, null);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<?> handleInsufficientStockException(InsufficientStockException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAll(Exception ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request,null);
    }

    private ResponseEntity<?> buildResponse(HttpStatus status, String message, HttpServletRequest request, List<String> errors) {
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                errors
        );


        return new ResponseEntity<>(response, status);
    }
}
