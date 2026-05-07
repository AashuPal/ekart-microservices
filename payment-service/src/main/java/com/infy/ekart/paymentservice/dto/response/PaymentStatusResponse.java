package com.infy.ekart.paymentservice.dto.response;

public class PaymentStatusResponse {
    private String orderId;
    private String paymentId;
    private String status;
    private String transactionId;

    public PaymentStatusResponse(String orderId, String paymentId, String status, String transactionId) {
        this.orderId = orderId;
        this.paymentId = paymentId;
        this.status = status;
        this.transactionId = transactionId;
    }

    public String getOrderId() { return orderId; }
    public String getPaymentId() { return paymentId; }
    public String getStatus() { return status; }
    public String getTransactionId() { return transactionId; }
}