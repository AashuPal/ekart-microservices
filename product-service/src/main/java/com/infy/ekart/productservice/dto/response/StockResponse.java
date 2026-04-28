package com.infy.ekart.productservice.dto.response;

import java.util.UUID;

public class StockResponse {

    private UUID inventoryId;
    private UUID productId;
    private Integer totalQuantity;
    private Integer availableQuantity;
    private String stockStatus;

    public StockResponse(UUID inventoryId, UUID productId, Integer totalQuantity,
                         Integer availableQuantity, String stockStatus) {
        this.inventoryId = inventoryId;
        this.productId = productId;
        this.totalQuantity = totalQuantity;
        this.availableQuantity = availableQuantity;
        this.stockStatus = stockStatus;
    }

    // Getters
    public UUID getInventoryId() { return inventoryId; }
    public UUID getProductId() { return productId; }
    public Integer getTotalQuantity() { return totalQuantity; }
    public Integer getAvailableQuantity() { return availableQuantity; }
    public String getStockStatus() { return stockStatus; }
}