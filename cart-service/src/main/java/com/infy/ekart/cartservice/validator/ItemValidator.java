package com.infy.ekart.cartservice.validator;

import com.infy.ekart.cartservice.exception.InvalidQuantityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ItemValidator {

    private static final Logger log = LoggerFactory.getLogger(ItemValidator.class);

    public void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new InvalidQuantityException(quantity);
        }
    }

    public void validateQuantityRange(int quantity, int minQuantity, int maxQuantity) {
        if (quantity < minQuantity) {
            throw new InvalidQuantityException("Quantity must be at least " + minQuantity);
        }
        if (quantity > maxQuantity) {
            throw new InvalidQuantityException("Quantity cannot exceed " + maxQuantity);
        }
    }
}