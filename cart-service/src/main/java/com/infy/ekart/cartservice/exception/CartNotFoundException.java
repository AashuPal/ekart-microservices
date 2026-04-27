package com.infy.ekart.cartservice.exception;

import java.util.UUID;

public class CartNotFoundException extends RuntimeException {
    
    public CartNotFoundException(String message) {
        super(message);
    }

    public CartNotFoundException(UUID cartId) {
        super("Cart not found with ID: " + cartId);
    }

    public CartNotFoundException(UUID cartId, Throwable cause) {
        super("Cart not found with ID: " + cartId, cause);
    }
}