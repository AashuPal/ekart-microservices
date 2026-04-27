package com.infy.ekart.cartservice.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CartItemResponse(
    UUID itemId,
    UUID productId,
    UUID skuId,
    String productName,
    String imageUrl,
    Integer quantity,
    BigDecimal unitPrice,
    BigDecimal totalPrice,
    LocalDateTime addedAt,
    LocalDateTime updatedAt
) {}