package com.infy.ekart.cartservice.event.listener;

import com.infy.ekart.cartservice.service.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

//@Component
public class OrderEventListener {

    private static final Logger log = LoggerFactory.getLogger(OrderEventListener.class);
    private final CartService cartService;

    public OrderEventListener(CartService cartService) {
        this.cartService = cartService;
    }

    @KafkaListener(topics = "${app.kafka.topics.order-created:order-created}", 
                   groupId = "cart-service-group")
    public void handleOrderCreated(String message) {
        log.info("Order created event received: {}", message);
    }

    @KafkaListener(topics = "${app.kafka.topics.order-cancelled:order-cancelled}", 
                   groupId = "cart-service-group")
    public void handleOrderCancelled(String message) {
        log.info("Order cancelled event received: {}", message);
    }
}