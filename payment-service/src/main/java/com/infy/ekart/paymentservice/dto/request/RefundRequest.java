package com.infy.ekart.paymentservice.dto.request;

public class RefundRequest {
    private String paymentId;
    private String reason;

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}