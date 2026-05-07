package com.infy.ekart.cartservice.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CheckoutResponse(
    UUID cartId,
    UUID orderId,
    String orderNumber,
    BigDecimal subtotal,
    BigDecimal discount,
    BigDecimal tax,
    BigDecimal shipping,
    BigDecimal totalAmount,
    String currencyCode,
    String status,
    LocalDateTime createdAt
) {}