package com.infy.ekart.cartservice.dto.response;

import java.math.BigDecimal;

public record CartSummaryResponse(
    String cartId,
    Integer totalItems,
    BigDecimal subtotal,
    BigDecimal discount,
    BigDecimal totalAmount
) {}