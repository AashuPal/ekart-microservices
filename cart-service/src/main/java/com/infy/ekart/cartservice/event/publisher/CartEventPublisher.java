package com.infy.ekart.cartservice.event.publisher;

import com.infy.ekart.cartservice.dto.event.CartEvent;

public interface CartEventPublisher {
    
    void publishCartCreatedEvent(String cartId, String userId);
    
    void publishItemAddedEvent(String cartId, String itemId, String productId, int quantity);
    
    void publishItemRemovedEvent(String cartId, String itemId, String productId);
    
    void publishCartConvertedEvent(String cartId, String orderId);
    
    void publishCartAbandonedEvent(String cartId, String userId);
    
    void publishCartExpiredEvent(String cartId, String userId);
    
    void publishEvent(CartEvent event);
}