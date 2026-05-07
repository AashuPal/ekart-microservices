package com.infy.ekart.paymentservice.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class OrderServiceClient {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceClient.class);
    private final WebClient webClient;

    @Value("${app.client.order-service.url:http://localhost:8086}")
    private String orderServiceUrl;

    public OrderServiceClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public void updateOrderPaymentStatus(String orderId, String paymentId, String status) {
        try {
            webClient.put()
                .uri(orderServiceUrl + "/api/v1/orders/{orderId}/payment-status", orderId)
                .bodyValue(new PaymentStatusUpdate(paymentId, status))
                .retrieve()
                .bodyToMono(Void.class)
                .subscribe();
            log.info("Order {} payment status updated to {}", orderId, status);
        } catch (Exception e) {
            log.error("Error updating order payment status: {}", e.getMessage());
        }
    }

    private record PaymentStatusUpdate(String paymentId, String status) {}
}