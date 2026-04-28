package com.infy.ekart.productservice.dto.event;

import java.util.UUID;

public class ProductUpdatedEvent {
    private UUID productId;
    private String name;
    private String type;

    public ProductUpdatedEvent(UUID productId, String name, String type) {
        this.productId = productId;
        this.name = name;
        this.type = type;
    }

    public UUID getProductId() { return productId; }
    public String getName() { return name; }
    public String getType() { return type; }
}