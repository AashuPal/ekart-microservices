package com.infy.ekart.cartservice.event.listener;

import com.infy.ekart.cartservice.cache.PricingCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

//@Component
public class PricingEventListener {

    private static final Logger log = LoggerFactory.getLogger(PricingEventListener.class);
    private final PricingCacheManager pricingCacheManager;

    public PricingEventListener(PricingCacheManager pricingCacheManager) {
        this.pricingCacheManager = pricingCacheManager;
    }

    @KafkaListener(topics = "${app.kafka.topics.price-updated:price-updated}", 
                   groupId = "cart-service-group")
    public void handlePriceUpdated(String message) {
        log.info("Price update event received: {}", message);
    }
}