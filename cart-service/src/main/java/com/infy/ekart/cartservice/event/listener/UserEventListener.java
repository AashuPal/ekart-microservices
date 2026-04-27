package com.infy.ekart.cartservice.event.listener;

import com.infy.ekart.cartservice.service.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

//@Component
public class UserEventListener {

    private static final Logger log = LoggerFactory.getLogger(UserEventListener.class);
    private final CartService cartService;

    public UserEventListener(CartService cartService) {
        this.cartService = cartService;
    }

    @KafkaListener(topics = "${app.kafka.topics.user-logged-in:user-logged-in}", 
                   groupId = "cart-service-group")
    public void handleUserLoggedIn(String message) {
        log.info("User logged in event received: {}", message);
    }

    @KafkaListener(topics = "${app.kafka.topics.user-logged-out:user-logged-out}", 
                   groupId = "cart-service-group")
    public void handleUserLoggedOut(String message) {
        log.info("User logged out event received: {}", message);
    }
}