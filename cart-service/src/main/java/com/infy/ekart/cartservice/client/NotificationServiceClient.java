package com.infy.ekart.cartservice.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

@Component
public class NotificationServiceClient {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceClient.class);
    private final WebClient webClient;

    @Value("${app.client.notification-service.url:http://localhost:8087}")
    private String notificationServiceUrl;

    public NotificationServiceClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public void sendAbandonedCartReminder(UUID userId, UUID cartId, String recoveryUrl) {
        try {
            webClient.post()
                .uri(notificationServiceUrl + "/api/v1/notifications/abandoned-cart")
                .bodyValue(new AbandonedCartNotification(userId, cartId, recoveryUrl))
                .retrieve()
                .bodyToMono(Void.class)
                .subscribe();
            log.info("Sent abandoned cart reminder for cart: {}", cartId);
        } catch (Exception e) {
            log.error("Error sending abandoned cart notification: {}", e.getMessage());
        }
    }

    private record AbandonedCartNotification(UUID userId, UUID cartId, String recoveryUrl) {}
}