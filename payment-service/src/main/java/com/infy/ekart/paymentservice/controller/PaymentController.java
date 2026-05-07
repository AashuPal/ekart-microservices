package com.infy.ekart.paymentservice.controller;

import com.infy.ekart.paymentservice.dto.request.PaymentRequest;
import com.infy.ekart.paymentservice.dto.request.RefundRequest;
import com.infy.ekart.paymentservice.dto.response.PaymentResponse;
import com.infy.ekart.paymentservice.dto.response.PaymentStatusResponse;
import com.infy.ekart.paymentservice.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<PaymentResponse> processPayment(@RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.processPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable String paymentId) {
        PaymentResponse response = paymentService.getPaymentById(paymentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentStatusResponse> getPaymentByOrder(@PathVariable String orderId) {
        PaymentStatusResponse response = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserPayments(@PathVariable String userId) {
        return ResponseEntity.ok(paymentService.getUserPayments(userId));
    }

    @PostMapping("/{paymentId}/refund")
    public ResponseEntity<?> refundPayment(@PathVariable String paymentId, @RequestBody RefundRequest request) {
        return ResponseEntity.ok(paymentService.refundPayment(paymentId, request));
    }

    @GetMapping("/verify/{transactionId}")
    public ResponseEntity<PaymentStatusResponse> verifyPayment(@PathVariable String transactionId) {
        PaymentStatusResponse response = paymentService.verifyPayment(transactionId);
        return ResponseEntity.ok(response);
    }
}