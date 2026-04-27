package com.infy.ekart.cartservice.dto.event;

import java.time.Instant;
import java.util.UUID;

public record ItemRemovedEvent(
    UUID cartId,
    UUID itemId,
    UUID productId,
    String eventType,
    Instant timestamp
) implements CartEvent {
    
    public ItemRemovedEvent {
        timestamp = Instant.now();
    }
}