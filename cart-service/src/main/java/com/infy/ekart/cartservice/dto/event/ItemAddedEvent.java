package com.infy.ekart.cartservice.dto.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ItemAddedEvent(
    UUID cartId,
    UUID itemId,
    UUID productId,
    UUID skuId,
    int quantity,
    BigDecimal unitPrice,
    String eventType,
    Instant timestamp
) implements CartEvent {
    
    public ItemAddedEvent {
        timestamp = Instant.now();
    }
}