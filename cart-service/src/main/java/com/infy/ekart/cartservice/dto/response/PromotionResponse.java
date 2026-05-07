package com.infy.ekart.cartservice.dto.response;

import com.infy.ekart.cartservice.enums.DiscountType;
import java.math.BigDecimal;
import java.util.UUID;

public record PromotionResponse(
    UUID promotionId,
    String couponCode,
    DiscountType discountType,
    BigDecimal discountAmount,
    String status
) {}