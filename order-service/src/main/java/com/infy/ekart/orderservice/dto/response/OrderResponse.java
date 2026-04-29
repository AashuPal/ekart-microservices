package com.infy.ekart.orderservice.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderResponse {
    private UUID id;
    private String orderNumber;
    private UUID userId;
    private String userEmail;
    private String userName;
    private String status;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal tax;
    private BigDecimal shipping;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private String paymentStatus;
    private String trackingNumber;
    private String notes;
    private List<OrderItemResponse> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime cancelledAt;

    public OrderResponse(UUID id, String orderNumber, UUID userId, String userEmail,
                         String userName, String status, BigDecimal subtotal, BigDecimal discount,
                         BigDecimal tax, BigDecimal shipping, BigDecimal totalAmount,
                         String paymentMethod, String paymentStatus, String trackingNumber,
                         String notes, List<OrderItemResponse> items, LocalDateTime createdAt,
                         LocalDateTime updatedAt, LocalDateTime deliveredAt, LocalDateTime cancelledAt) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.userId = userId;
        this.userEmail = userEmail;
        this.userName = userName;
        this.status = status;
        this.subtotal = subtotal;
        this.discount = discount;
        this.tax = tax;
        this.shipping = shipping;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.trackingNumber = trackingNumber;
        this.notes = notes;
        this.items = items;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deliveredAt = deliveredAt;
        this.cancelledAt = cancelledAt;
    }

    // Getters
    public UUID getId() { return id; }
    public String getOrderNumber() { return orderNumber; }
    public UUID getUserId() { return userId; }
    public String getUserEmail() { return userEmail; }
    public String getUserName() { return userName; }
    public String getStatus() { return status; }
    public BigDecimal getSubtotal() { return subtotal; }
    public BigDecimal getDiscount() { return discount; }
    public BigDecimal getTax() { return tax; }
    public BigDecimal getShipping() { return shipping; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getPaymentStatus() { return paymentStatus; }
    public String getTrackingNumber() { return trackingNumber; }
    public String getNotes() { return notes; }
    public List<OrderItemResponse> getItems() { return items; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public LocalDateTime getDeliveredAt() { return deliveredAt; }
    public LocalDateTime getCancelledAt() { return cancelledAt; }
}