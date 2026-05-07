package com.infy.ekart.paymentservice.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class PaymentResponse {
    private UUID id;
    private String paymentNumber;
    private UUID orderId;
    private UUID userId;
    private BigDecimal amount;
    private String currency;
    private String paymentMethod;
    private String status;
    private String transactionId;
    private String upiId;
    private String cardLastFour;
    private String cardType;
    private String bankName;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;

    public PaymentResponse(UUID id, String paymentNumber, UUID orderId, UUID userId,
                          BigDecimal amount, String currency, String paymentMethod,
                          String status, String transactionId, String upiId,
                          String cardLastFour, String cardType, String bankName,
                          LocalDateTime paidAt, LocalDateTime createdAt) {
        this.id = id;
        this.paymentNumber = paymentNumber;
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
        this.currency = currency;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.transactionId = transactionId;
        this.upiId = upiId;
        this.cardLastFour = cardLastFour;
        this.cardType = cardType;
        this.bankName = bankName;
        this.paidAt = paidAt;
        this.createdAt = createdAt;
    }

    // Getters
    public UUID getId() { return id; }
    public String getPaymentNumber() { return paymentNumber; }
    public UUID getOrderId() { return orderId; }
    public UUID getUserId() { return userId; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getStatus() { return status; }
    public String getTransactionId() { return transactionId; }
    public String getUpiId() { return upiId; }
    public String getCardLastFour() { return cardLastFour; }
    public String getCardType() { return cardType; }
    public String getBankName() { return bankName; }
    public LocalDateTime getPaidAt() { return paidAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}