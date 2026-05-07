package com.infy.ekart.cartservice.dto.response;

import java.math.BigDecimal;

public record PricingResponse(
    BigDecimal subtotal,
    BigDecimal discount,
    BigDecimal tax,
    BigDecimal shipping,
    BigDecimal totalAmount,
    String currencyCode
) {}