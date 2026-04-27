package com.infy.ekart.cartservice.exception;

public class InvalidQuantityException extends RuntimeException {
    
    public InvalidQuantityException(String message) {
        super(message);
    }

    public InvalidQuantityException(int quantity) {
        super("Invalid quantity: " + quantity + ". Quantity must be greater than 0");
    }
}