package com.infy.ekart.cartservice.dto.event;

import java.time.Instant;
import java.util.UUID;

public sealed interface CartEvent permits 
    CartCreatedEvent,
    ItemAddedEvent,
    ItemRemovedEvent,
    CartConvertedEvent,
    CartExpiredEvent,
    CartAbandonedEvent {
    
    UUID cartId();
    String eventType();
    Instant timestamp();
}