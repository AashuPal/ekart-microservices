package com.infy.ekart.cartservice.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

@Component
public class InventoryServiceClient {

    private static final Logger log = LoggerFactory.getLogger(InventoryServiceClient.class);
    private final WebClient webClient;  // Changed from Builder to WebClient

    @Value("${app.client.inventory-service.url:http://localhost:8083}")
    private String inventoryServiceUrl;

    // Constructor injection
    public InventoryServiceClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();  // Build WebClient once
    }

    public boolean checkStock(UUID skuId, int quantity) {
        try {
            return Boolean.TRUE.equals(
                webClient.get()
                    .uri(inventoryServiceUrl + "/api/v1/inventory/{skuId}/check?quantity={qty}", skuId, quantity)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block()
            );
        } catch (Exception e) {
            log.error("Error checking stock for SKU {}: {}", skuId, e.getMessage());
            return false;
        }
    }

    public void reserveStock(UUID skuId, int quantity) {
        try {
            webClient.post()
                .uri(inventoryServiceUrl + "/api/v1/inventory/{skuId}/reserve", skuId)
                .bodyValue(new ReserveStockRequest(quantity))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
            log.info("Reserved {} units of SKU {}", quantity, skuId);
        } catch (Exception e) {
            log.error("Error reserving stock for SKU {}: {}", skuId, e.getMessage());
        }
    }

    public void releaseStock(UUID skuId, int quantity) {
        try {
            webClient.post()
                .uri(inventoryServiceUrl + "/api/v1/inventory/{skuId}/release", skuId)
                .bodyValue(new ReserveStockRequest(quantity))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
            log.info("Released {} units of SKU {}", quantity, skuId);
        } catch (Exception e) {
            log.error("Error releasing stock for SKU {}: {}", skuId, e.getMessage());
        }
    }

    private record ReserveStockRequest(int quantity) {}
}