package com.infy.ekart.cartservice.exception;

import java.util.UUID;

public class CartExpiredException extends RuntimeException {
    
    public CartExpiredException(String message) {
        super(message);
    }

    public CartExpiredException(UUID cartId) {
        super("Cart has expired: " + cartId);
    }
}