package com.infy.ekart.notificationservice.controller;

import com.infy.ekart.notificationservice.dto.request.EmailRequest;
import com.infy.ekart.notificationservice.dto.response.EmailResponse;
import com.infy.ekart.notificationservice.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/email")
public class NotificationController {

    private final EmailService emailService;

    public NotificationController(EmailService emailService) {
        this.emailService = emailService;
    }

    @Operation(summary = "Send order confirmation email")
    @PostMapping("/order-confirmation")
    public ResponseEntity<EmailResponse> sendOrderConfirmation(@RequestBody EmailRequest request) {
        boolean sent = emailService.sendOrderConfirmation(
                request.getTo(),
                request.getCustomerName(),
                request.getOrderNumber(),
                request.getOrderTotal(),
                request.getItemList(),
                request.getTrackingNumber()
        );
        return ResponseEntity.ok(new EmailResponse(sent, sent ? "Order confirmation sent!" : "Failed to send"));
    }

    @Operation(summary = "Send order status update email")
    @PostMapping("/order-status")
    public ResponseEntity<EmailResponse> sendOrderStatus(@RequestBody EmailRequest request) {
        boolean sent = emailService.sendOrderStatusUpdate(
                request.getTo(),
                request.getCustomerName(),
                request.getOrderNumber(),
                request.getStatus(),
                request.getTrackingNumber()
        );
        return ResponseEntity.ok(new EmailResponse(sent, sent ? "Order status update sent!" : "Failed to send"));
    }

    @Operation(summary = "Send payment confirmation email")
    @PostMapping("/payment-confirmation")
    public ResponseEntity<EmailResponse> sendPaymentConfirmation(@RequestBody EmailRequest request) {
        boolean sent = emailService.sendPaymentConfirmation(
                request.getTo(),
                request.getCustomerName(),
                request.getOrderNumber(),
                request.getOrderTotal(),
                request.getPaymentMethod(),
                request.getTransactionId()
        );
        return ResponseEntity.ok(new EmailResponse(sent, sent ? "Payment confirmation sent!" : "Failed to send"));
    }

    @Operation(summary = "Send refund confirmation email")
    @PostMapping("/refund")
    public ResponseEntity<EmailResponse> sendRefundConfirmation(@RequestBody EmailRequest request) {
        boolean sent = emailService.sendRefundConfirmation(
                request.getTo(),
                request.getCustomerName(),
                request.getOrderNumber(),
                request.getRefundAmount(),
                request.getRefundReason()
        );
        return ResponseEntity.ok(new EmailResponse(sent, sent ? "Refund confirmation sent!" : "Failed to send"));
    }

    @Operation(summary = "Send generic email")
    @PostMapping("/send")
    public ResponseEntity<EmailResponse> sendEmail(@RequestBody EmailRequest request) {
        boolean sent = emailService.sendEmail(request.getTo(), request.getSubject(), request.getBody());
        return ResponseEntity.ok(new EmailResponse(sent, sent ? "Email sent!" : "Failed to send"));
    }
}