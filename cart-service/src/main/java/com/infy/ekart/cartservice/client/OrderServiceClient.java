package com.infy.ekart.cartservice.client;

import com.infy.ekart.cartservice.entity.Cart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

@Component
public class OrderServiceClient {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceClient.class);
    private final WebClient webClient;

    @Value("${app.client.order-service.url:http://localhost:8086}")
    private String orderServiceUrl;

    public OrderServiceClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public OrderResponse createOrder(Cart cart, Object checkoutRequest) {
        try {
            return webClient.post()
                .uri(orderServiceUrl + "/api/v1/orders")
                .bodyValue(new CreateOrderRequest(cart.getId(), cart.getUserId()))
                .retrieve()
                .bodyToMono(OrderResponse.class)
                .block();
        } catch (Exception e) {
            log.error("Error creating order: {}", e.getMessage());
            throw new RuntimeException("Failed to create order", e);
        }
    }

    private record CreateOrderRequest(UUID cartId, UUID userId) {}

    public record OrderResponse(UUID orderId, String orderNumber, String status) {}
}