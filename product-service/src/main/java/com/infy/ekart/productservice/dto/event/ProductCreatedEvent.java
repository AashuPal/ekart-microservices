package com.infy.ekart.productservice.dto.event;

import java.util.UUID;

public class ProductCreatedEvent {
    private UUID productId;
    private String name;
    private String sku;

    public ProductCreatedEvent(UUID productId, String name, String sku) {
        this.productId = productId;
        this.name = name;
        this.sku = sku;
    }

    public UUID getProductId() { return productId; }
    public String getName() { return name; }
    public String getSku() { return sku; }
}