package com.infy.ekart.cartservice.dto.response;

import com.infy.ekart.cartservice.enums.CartStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CartResponse(
    UUID cartId,
    UUID userId,
    UUID sessionId,
    CartStatus status,
    Integer totalItems,
    BigDecimal subtotal,
    BigDecimal discount,
    BigDecimal totalAmount,
    String currencyCode,
    List<CartItemResponse> items,
    List<PromotionResponse> promotions,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime expiresAt
) {}