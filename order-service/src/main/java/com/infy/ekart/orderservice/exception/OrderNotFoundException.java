package com.infy.ekart.orderservice.exception;

import java.util.UUID;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(String message) {
        super(message);
    }

    public OrderNotFoundException(UUID orderId) {
        super("Order not found with ID: " + orderId);
    }
}