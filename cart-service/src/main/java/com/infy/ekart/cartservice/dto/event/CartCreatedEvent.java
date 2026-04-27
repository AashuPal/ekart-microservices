package com.infy.ekart.cartservice.dto.event;

import java.time.Instant;
import java.util.UUID;

public record CartCreatedEvent(
    UUID cartId,
    UUID userId,
    UUID sessionId,
    String eventType,
    Instant timestamp
) implements CartEvent {
    
    public CartCreatedEvent {
        timestamp = Instant.now();
    }

    public CartCreatedEvent(UUID cartId, UUID userId, UUID sessionId) {
        this(cartId, userId, sessionId, "CART_CREATED", Instant.now());
    }
}