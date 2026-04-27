package com.infy.ekart.cartservice.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

@Component
public class UserServiceClient {

    private static final Logger log = LoggerFactory.getLogger(UserServiceClient.class);
    private final WebClient webClient;

    @Value("${app.client.user-service.url:http://localhost:8081}")
    private String userServiceUrl;

    public UserServiceClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public UserResponse getUserDetails(UUID userId) {
        try {
            return webClient.get()
                .uri(userServiceUrl + "/api/v1/users/{userId}", userId)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .block();
        } catch (Exception e) {
            log.error("Error getting user details for {}: {}", userId, e.getMessage());
            return null;
        }
    }

    public boolean validateUser(UUID userId) {
        try {
            return Boolean.TRUE.equals(
                webClient.get()
                    .uri(userServiceUrl + "/api/v1/users/{userId}/exists", userId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block()
            );
        } catch (Exception e) {
            log.error("Error validating user {}: {}", userId, e.getMessage());
            return false;
        }
    }

    public record UserResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        String phoneNumber
    ) {}
}