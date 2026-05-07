package com.infy.ekart.cartservice.dto.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CartConvertedEvent(
    UUID cartId,
    UUID orderId,
    String orderNumber,
    BigDecimal totalAmount,
    String eventType,
    Instant timestamp
) implements CartEvent {
    
    public CartConvertedEvent {
        timestamp = Instant.now();
    }
}