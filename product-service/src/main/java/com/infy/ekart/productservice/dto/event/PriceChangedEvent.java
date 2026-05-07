package com.infy.ekart.productservice.dto.event;

import java.math.BigDecimal;
import java.util.UUID;

public class PriceChangedEvent {
    private UUID productId;
    private String sku;
    private BigDecimal oldPrice;
    private BigDecimal newPrice;

    public PriceChangedEvent(UUID productId, String sku, BigDecimal oldPrice, BigDecimal newPrice) {
        this.productId = productId;
        this.sku = sku;
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
    }

    public UUID getProductId() { return productId; }
    public String getSku() { return sku; }
    public BigDecimal getOldPrice() { return oldPrice; }
    public BigDecimal getNewPrice() { return newPrice; }
}