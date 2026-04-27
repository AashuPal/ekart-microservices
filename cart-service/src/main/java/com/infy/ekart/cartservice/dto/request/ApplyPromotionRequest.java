package com.infy.ekart.cartservice.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ApplyPromotionRequest(
    @NotBlank(message = "Coupon code is required")
    String couponCode
) {}