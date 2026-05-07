package com.infy.ekart.cartservice.exception;

import com.infy.ekart.cartservice.dto.response.ErrorResponse;
import com.infy.ekart.cartservice.event.listener.PricingEventListener;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
	
	 private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(CartNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleCartNotFoundException(CartNotFoundException ex, HttpServletRequest request) {
        log.error("Cart not found: {}", ex.getMessage());
        return new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            "Cart Not Found",
            ex.getMessage(),
            request.getRequestURI()
        );
    }

    @ExceptionHandler(ItemNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleItemNotFoundException(ItemNotFoundException ex, HttpServletRequest request) {
        log.error("Item not found: {}", ex.getMessage());
        return new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            "Item Not Found",
            ex.getMessage(),
            request.getRequestURI()
        );
    }

    @ExceptionHandler(InsufficientStockException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleInsufficientStockException(InsufficientStockException ex, HttpServletRequest request) {
        log.error("Insufficient stock: {}", ex.getMessage());
        return new ErrorResponse(
            HttpStatus.CONFLICT.value(),
            "Insufficient Stock",
            ex.getMessage(),
            request.getRequestURI()
        );
    }

    @ExceptionHandler(InvalidQuantityException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidQuantityException(InvalidQuantityException ex, HttpServletRequest request) {
        log.error("Invalid quantity: {}", ex.getMessage());
        return new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Invalid Quantity",
            ex.getMessage(),
            request.getRequestURI()
        );
    }

    @ExceptionHandler(CartExpiredException.class)
    @ResponseStatus(HttpStatus.GONE)
    public ErrorResponse handleCartExpiredException(CartExpiredException ex, HttpServletRequest request) {
        log.error("Cart expired: {}", ex.getMessage());
        return new ErrorResponse(
            HttpStatus.GONE.value(),
            "Cart Expired",
            ex.getMessage(),
            request.getRequestURI()
        );
    }

    @ExceptionHandler(PromotionNotApplicableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlePromotionNotApplicableException(PromotionNotApplicableException ex, HttpServletRequest request) {
        log.error("Promotion not applicable: {}", ex.getMessage());
        return new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Promotion Not Applicable",
            ex.getMessage(),
            request.getRequestURI()
        );
    }

    @ExceptionHandler(CheckoutException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleCheckoutException(CheckoutException ex, HttpServletRequest request) {
        log.error("Checkout failed: {}", ex.getMessage());
        return new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Checkout Failed",
            ex.getMessage(),
            request.getRequestURI()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        log.error("Validation failed: {}", errors);
        return new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Validation Failed",
            "Invalid request parameters",
            request.getRequestURI(),
            errors
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        log.error("Invalid argument: {}", ex.getMessage());
        return new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Invalid Argument",
            ex.getMessage(),
            request.getRequestURI()
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error: ", ex);
        return new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            "An unexpected error occurred. Please try again later.",
            request.getRequestURI()
        );
    }
}