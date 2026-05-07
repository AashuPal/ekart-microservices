package com.infy.ekart.cartservice.exception;

import java.util.UUID;

public class ItemNotFoundException extends RuntimeException {
    
    public ItemNotFoundException(String message) {
        super(message);
    }

    public ItemNotFoundException(UUID itemId) {
        super("Cart item not found with ID: " + itemId);
    }

    public ItemNotFoundException(UUID cartId, UUID itemId) {
        super("Item " + itemId + " not found in cart " + cartId);
    }
}