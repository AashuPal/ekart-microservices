package com.infy.ekart.productservice.validator;

import com.infy.ekart.productservice.dto.request.CreateProductRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ProductValidator {

    private static final Logger log = LoggerFactory.getLogger(ProductValidator.class);

    public void validateCreateProduct(CreateProductRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name is required");
        }
        if (request.getSku() == null || request.getSku().trim().isEmpty()) {
            throw new IllegalArgumentException("SKU is required");
        }
        if (request.getBasePrice() != null && request.getBasePrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Base price must be greater than 0");
        }
        if (request.getSellingPrice() != null && request.getSellingPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Selling price must be greater than 0");
        }
    }
}