package com.infy.ekart.cartservice.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateCartRequest(
    UUID userId,
    UUID sessionId,
    String countryCode,
    String currencyCode
) {
    public CreateCartRequest {
        if (userId == null && sessionId == null) {
            throw new IllegalArgumentException("Either userId or sessionId must be provided");
        }
    }
}