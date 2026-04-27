package com.infy.ekart.cartservice.validator;

import com.infy.ekart.cartservice.entity.Cart;
import com.infy.ekart.cartservice.exception.CheckoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CheckoutValidator {

    private static final Logger log = LoggerFactory.getLogger(CheckoutValidator.class);

    public void validateCartForCheckout(Cart cart) {
        if (cart.getItems().isEmpty()) {
            throw new CheckoutException("Cannot checkout an empty cart");
        }

        cart.getItems().forEach(item -> {
            if (item.getQuantity() <= 0) {
                throw new CheckoutException("Invalid quantity for product: " + item.getProductId());
            }
            if (item.getUnitPrice() == null || item.getUnitPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                throw new CheckoutException("Invalid price for product: " + item.getProductId());
            }
        });
    }
}