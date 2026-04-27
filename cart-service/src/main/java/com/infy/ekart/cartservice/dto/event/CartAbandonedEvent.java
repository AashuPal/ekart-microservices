package com.infy.ekart.cartservice.dto.event;

import java.time.Instant;
import java.util.UUID;

public record CartAbandonedEvent(
    UUID cartId,
    UUID userId,
    String eventType,
    Instant timestamp
) implements CartEvent {
    
    public CartAbandonedEvent {
        timestamp = Instant.now();
    }
}