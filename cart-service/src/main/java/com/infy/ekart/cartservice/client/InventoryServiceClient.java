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
    private final WebClient webClient;

    @Value("${app.client.inventory-service.url:http://localhost:8083}")
    private String inventoryServiceUrl;

    public InventoryServiceClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public boolean checkStock(UUID skuId, int quantity) {
        try {
            // ✅ FIXED: Call Product Service stock API
            String url = inventoryServiceUrl + "/api/v1/products/" + skuId + "/stock";
            
            StockResponse response = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(StockResponse.class)
                .block();
            
            if (response != null && response.availableQuantity >= quantity) {
                log.info("Stock OK: {} (requested: {})", response.availableQuantity, quantity);
                return true;
            }
            log.warn("Low stock: {} (requested: {})", 
                response != null ? response.availableQuantity : 0, quantity);
            return false;
        } catch (Exception e) {
            log.error("Stock check failed: {}. Allowing anyway.", e.getMessage());
            return true; // ✅ Allow if service unavailable
        }
    }

    public void reserveStock(UUID skuId, int quantity) {
        try {
            // Get current stock
            String getUrl = inventoryServiceUrl + "/api/v1/products/" + skuId + "/stock";
            StockResponse response = webClient.get()
                .uri(getUrl)
                .retrieve()
                .bodyToMono(StockResponse.class)
                .block();
            
            if (response != null) {
                int newQty = response.availableQuantity - quantity;
                if (newQty < 0) newQty = 0;
                
                // Update stock
                String putUrl = inventoryServiceUrl + "/api/v1/products/" + skuId + "/stock";
                webClient.put()
                    .uri(putUrl)
                    .bodyValue("{\"quantity\":" + newQty + "}")
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
                log.info("Reserved {} units, new stock: {}", quantity, newQty);
            }
        } catch (Exception e) {
            log.error("Reserve failed: {}", e.getMessage());
        }
    }

    public void releaseStock(UUID skuId, int quantity) {
        try {
            String getUrl = inventoryServiceUrl + "/api/v1/products/" + skuId + "/stock";
            StockResponse response = webClient.get()
                .uri(getUrl)
                .retrieve()
                .bodyToMono(StockResponse.class)
                .block();
            
            if (response != null) {
                int newQty = response.availableQuantity + quantity;
                
                String putUrl = inventoryServiceUrl + "/api/v1/products/" + skuId + "/stock";
                webClient.put()
                    .uri(putUrl)
                    .bodyValue("{\"quantity\":" + newQty + "}")
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
                log.info("Released {} units, new stock: {}", quantity, newQty);
            }
        } catch (Exception e) {
            log.error("Release failed: {}", e.getMessage());
        }
    }

    // Helper class for stock response
    private static class StockResponse {
        public int totalQuantity;
        public int availableQuantity;
        public String stockStatus;
    }
}