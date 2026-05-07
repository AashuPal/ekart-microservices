package com.infy.ekart.paymentservice.exception;

public class PaymentFailedException extends RuntimeException {
    
    public PaymentFailedException(String message) {
        super(message);
    }
    
    public PaymentFailedException(String orderId, String reason) {
        super("Payment failed for order " + orderId + ": " + reason);
    }
}