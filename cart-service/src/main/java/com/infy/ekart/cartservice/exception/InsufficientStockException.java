package com.infy.ekart.cartservice.exception;

import java.util.UUID;

public class InsufficientStockException extends RuntimeException {
    
    public InsufficientStockException(String message) {
        super(message);
    }

    public InsufficientStockException(UUID productId, int requested, int available) {
        super(String.format("Insufficient stock for product %s. Requested: %d, Available: %d", 
            productId, requested, available));
    }
}