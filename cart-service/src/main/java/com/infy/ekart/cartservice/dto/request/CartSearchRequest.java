package com.infy.ekart.cartservice.dto.request;

import com.infy.ekart.cartservice.enums.CartStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record CartSearchRequest(
    UUID userId,
    UUID sessionId,
    CartStatus status,
    LocalDateTime createdAfter,
    LocalDateTime createdBefore
) {}