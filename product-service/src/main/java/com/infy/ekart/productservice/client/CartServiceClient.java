package com.infy.ekart.productservice.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class CartServiceClient {

    private static final Logger log = LoggerFactory.getLogger(CartServiceClient.class);

    private final WebClient webClient;

    @Value("${app.client.cart-service.url:http://localhost:8082}")
    private String cartServiceUrl;

    public CartServiceClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public void notifyProductUpdate(String productId) {
        try {
            webClient.post()
                .uri(cartServiceUrl + "/internal/products/{productId}/update", productId)
                .retrieve()
                .bodyToMono(Void.class)
                .subscribe();
        } catch (Exception e) {
            log.error("Error notifying cart service: {}", e.getMessage());
        }
    }
}