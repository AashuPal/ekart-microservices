package com.infy.ekart.cartservice.event.publisher.impl;

import com.infy.ekart.cartservice.dto.event.*;
import com.infy.ekart.cartservice.event.listener.PricingEventListener;
import com.infy.ekart.cartservice.event.publisher.CartEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

//@Component
//@RequiredArgsConstructor
//@Slf4j
public class CartEventPublisherImpl implements CartEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final Logger log = LoggerFactory.getLogger(CartEventPublisherImpl.class);

    @Value("${app.kafka.topics.cart-events:cart-events}")
    private String cartEventsTopic;

    @Override
    public void publishCartCreatedEvent(String cartId, String userId) {
        CartCreatedEvent event = new CartCreatedEvent(
            UUID.fromString(cartId), 
            userId != null ? UUID.fromString(userId) : null, 
            null
        );
        publishEvent(event);
    }

    @Override
    public void publishItemAddedEvent(String cartId, String itemId, String productId, int quantity) {
        ItemAddedEvent event = new ItemAddedEvent(
            UUID.fromString(cartId),
            UUID.fromString(itemId),
            UUID.fromString(productId),
            null,
            quantity,
            null,
            "ITEM_ADDED",
            java.time.Instant.now()
        );
        publishEvent(event);
    }

    @Override
    public void publishItemRemovedEvent(String cartId, String itemId, String productId) {
        ItemRemovedEvent event = new ItemRemovedEvent(
            UUID.fromString(cartId),
            UUID.fromString(itemId),
            UUID.fromString(productId),
            "ITEM_REMOVED",
            java.time.Instant.now()
        );
        publishEvent(event);
    }

    @Override
    public void publishCartConvertedEvent(String cartId, String orderId) {
        CartConvertedEvent event = new CartConvertedEvent(
            UUID.fromString(cartId),
            UUID.fromString(orderId),
            null,
            null,
            "CART_CONVERTED",
            java.time.Instant.now()
        );
        publishEvent(event);
    }

    @Override
    public void publishCartAbandonedEvent(String cartId, String userId) {
        CartAbandonedEvent event = new CartAbandonedEvent(
            UUID.fromString(cartId),
            userId != null ? UUID.fromString(userId) : null,
            "CART_ABANDONED",
            java.time.Instant.now()
        );
        publishEvent(event);
    }

    @Override
    public void publishCartExpiredEvent(String cartId, String userId) {
        CartExpiredEvent event = new CartExpiredEvent(
            UUID.fromString(cartId),
            userId != null ? UUID.fromString(userId) : null,
            "CART_EXPIRED",
            java.time.Instant.now()
        );
        publishEvent(event);
    }

    @Override
    public void publishEvent(CartEvent event) {
        try {
            kafkaTemplate.send(cartEventsTopic, event.cartId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish event: {}", ex.getMessage());
                    } else {
                        log.debug("Event published successfully: {}", event.eventType());
                    }
                });
        } catch (Exception e) {
            log.error("Error publishing event: {}", e.getMessage());
        }
    }
}