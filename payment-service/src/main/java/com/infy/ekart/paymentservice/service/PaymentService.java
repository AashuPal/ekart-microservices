package com.infy.ekart.paymentservice.service;

import com.infy.ekart.paymentservice.dto.request.PaymentRequest;
import com.infy.ekart.paymentservice.dto.request.RefundRequest;
import com.infy.ekart.paymentservice.dto.response.PaymentResponse;
import com.infy.ekart.paymentservice.dto.response.PaymentStatusResponse;

import java.util.List;

public interface PaymentService {
    
    PaymentResponse processPayment(PaymentRequest request);
    
    PaymentResponse getPaymentById(String paymentId);
    
    PaymentStatusResponse getPaymentByOrderId(String orderId);
    
    List<PaymentResponse> getUserPayments(String userId);
    
    PaymentResponse refundPayment(String paymentId, RefundRequest request);
    
    PaymentStatusResponse verifyPayment(String transactionId);
}