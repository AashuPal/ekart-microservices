package com.infy.ekart.cartservice.dto.request;

import com.infy.ekart.cartservice.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;

public record CheckoutRequest(
    @NotNull(message = "Shipping address is required")
    ShippingAddress shippingAddress,
    
    @NotNull(message = "Payment method is required")
    PaymentMethod paymentMethod,
    
    String notes
) {
    public record ShippingAddress(
        @NotNull String fullName,
        @NotNull String addressLine1,
        String addressLine2,
        @NotNull String city,
        @NotNull String state,
        @NotNull String postalCode,
        @NotNull String country,
        @NotNull String phoneNumber
    ) {}
}