package com.infy.ekart.notificationservice.dto.request;

import lombok.Data;

@Data
public class EmailRequest {
    private String to;
    private String subject;
    private String body;
    
    // Order fields
    private String customerName;
    private String orderNumber;
    private String orderTotal;
    private String itemList;
    private String trackingNumber;
    private String paymentMethod;
    private String transactionId;
    
    // Status update
    private String status;
    
    // Refund
    private String refundAmount;
    private String refundReason;
}