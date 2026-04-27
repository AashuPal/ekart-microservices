package com.infy.ekart.cartservice.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class PricingServiceClient {

    private static final Logger log = LoggerFactory.getLogger(PricingServiceClient.class);
    private final WebClient webClient;

    @Value("${app.client.pricing-service.url:http://localhost:8084}")
    private String pricingServiceUrl;

    public PricingServiceClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public BigDecimal getProductPrice(UUID skuId) {
        try {
            PriceResponse response = webClient.get()
                .uri(pricingServiceUrl + "/api/v1/prices/{skuId}", skuId)
                .retrieve()
                .bodyToMono(PriceResponse.class)
                .block();
            
            return response != null ? response.price() : BigDecimal.ZERO;
        } catch (Exception e) {
            log.error("Error getting price for SKU {}: {}", skuId, e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    private record PriceResponse(BigDecimal price, String currency) {}
}