package com.infy.ekart.cartservice.exception;

public class PromotionNotApplicableException extends RuntimeException {
    
    public PromotionNotApplicableException(String message) {
        super(message);
    }

    public PromotionNotApplicableException(String couponCode, String reason) {
        super("Promotion code '" + couponCode + "' is not applicable: " + reason);
    }
}