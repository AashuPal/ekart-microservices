package com.infy.ekart.productservice.dto.event;

import java.util.UUID;

public class StockUpdatedEvent {
    private UUID productId;
    private String sku;
    private int quantity;
    private int availableQuantity;
    private String stockStatus;

    public StockUpdatedEvent(UUID productId, String sku, int quantity, 
                              int availableQuantity, String stockStatus) {
        this.productId = productId;
        this.sku = sku;
        this.quantity = quantity;
        this.availableQuantity = availableQuantity;
        this.stockStatus = stockStatus;
    }

    public UUID getProductId() { return productId; }
    public String getSku() { return sku; }
    public int getQuantity() { return quantity; }
    public int getAvailableQuantity() { return availableQuantity; }
    public String getStockStatus() { return stockStatus; }
}