package com.infy.ekart.orderservice.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderItemResponse {
    private UUID id;
    private UUID productId;
    private UUID skuId;
    private String productName;
    private String imageUrl;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;

    public OrderItemResponse(UUID id, UUID productId, UUID skuId, String productName,
                             String imageUrl, Integer quantity, BigDecimal unitPrice,
                             BigDecimal totalPrice) {
        this.id = id;
        this.productId = productId;
        this.skuId = skuId;
        this.productName = productName;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getProductId() { return productId; }
    public UUID getSkuId() { return skuId; }
    public String getProductName() { return productName; }
    public String getImageUrl() { return imageUrl; }
    public Integer getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public BigDecimal getTotalPrice() { return totalPrice; }
}