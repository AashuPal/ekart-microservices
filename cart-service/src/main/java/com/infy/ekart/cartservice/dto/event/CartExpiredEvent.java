package com.infy.ekart.cartservice.dto.event;

import java.time.Instant;
import java.util.UUID;

public record CartExpiredEvent(
    UUID cartId,
    UUID userId,
    String eventType,
    Instant timestamp
) implements CartEvent {
    
    public CartExpiredEvent {
        timestamp = Instant.now();
    }
}