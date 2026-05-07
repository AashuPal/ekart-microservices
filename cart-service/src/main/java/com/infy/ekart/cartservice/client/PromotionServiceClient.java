package com.infy.ekart.cartservice.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;

@Component
public class PromotionServiceClient {

    private static final Logger log = LoggerFactory.getLogger(PromotionServiceClient.class);
    private final WebClient webClient;

    @Value("${app.client.promotion-service.url:http://localhost:8085}")
    private String promotionServiceUrl;

    public PromotionServiceClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public PromotionValidationResult validateCoupon(String couponCode, BigDecimal cartTotal) {
        try {
            return webClient.post()
                .uri(promotionServiceUrl + "/api/v1/promotions/validate")
                .bodyValue(new ValidateCouponRequest(couponCode, cartTotal))
                .retrieve()
                .bodyToMono(PromotionValidationResult.class)
                .block();
        } catch (Exception e) {
            log.error("Error validating coupon {}: {}", couponCode, e.getMessage());
            return new PromotionValidationResult(false, null, null, "Service unavailable");
        }
    }

    public record ValidateCouponRequest(String couponCode, BigDecimal cartTotal) {}

    public record PromotionValidationResult(
        boolean valid,
        String discountType,
        BigDecimal discountAmount,
        String message
    ) {}
}