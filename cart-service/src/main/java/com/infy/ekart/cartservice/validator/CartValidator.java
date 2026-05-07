package com.infy.ekart.cartservice.validator;

import com.infy.ekart.cartservice.entity.Cart;
import com.infy.ekart.cartservice.enums.CartStatus;
import com.infy.ekart.cartservice.exception.CartExpiredException;
import com.infy.ekart.cartservice.exception.CartNotFoundException;
import com.infy.ekart.cartservice.repository.CartRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CartValidator {

    private static final Logger log = LoggerFactory.getLogger(CartValidator.class);

    private final CartRepository cartRepository;

    // FIX: Initialize cartRepository in constructor
    public CartValidator(CartRepository cartRepository) {
        this.cartRepository = cartRepository;  // THIS WAS MISSING!
    }

    public Cart validateAndGetActiveCart(UUID cartId) {
        Cart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new CartNotFoundException(cartId));

        if (cart.getStatus() != CartStatus.ACTIVE) {
            throw new CartExpiredException("Cart is not active. Current status: " + cart.getStatus());
        }

        if (cart.getExpiresAt() != null && cart.getExpiresAt().isBefore(java.time.LocalDateTime.now())) {
            cart.setStatus(CartStatus.EXPIRED);
            cartRepository.save(cart);
            throw new CartExpiredException(cartId);
        }

        return cart;
    }

    public void validateMaxItemsLimit(Cart cart, int maxItems) {
        if (cart.getItems().size() >= maxItems) {
            throw new IllegalArgumentException("Cart has reached maximum items limit of " + maxItems);
        }
    }

    public void validateCartOwnership(Cart cart, UUID userId) {
        if (cart.getUserId() != null && !cart.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Cart does not belong to the specified user");
        }
    }
}