package com.infy.ekart.cartservice.event.listener;

import com.infy.ekart.cartservice.service.InventoryReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

//@Component
public class InventoryEventListener {

    private static final Logger log = LoggerFactory.getLogger(InventoryEventListener.class);
    private final InventoryReservationService inventoryReservationService;

    // Manual constructor
    public InventoryEventListener(InventoryReservationService inventoryReservationService) {
        this.inventoryReservationService = inventoryReservationService;
    }

    @KafkaListener(topics = "${app.kafka.topics.inventory-events:inventory-events}", 
                   groupId = "cart-service-group")
    public void handleInventoryEvent(String message) {
        log.info("Received inventory event: {}", message);
        // Handle inventory updates
    }

    @KafkaListener(topics = "${app.kafka.topics.stock-released:stock-released}", 
                   groupId = "cart-service-group")
    public void handleStockReleased(String message) {
        log.info("Stock released event received: {}", message);
        // Update reservations
    }
}