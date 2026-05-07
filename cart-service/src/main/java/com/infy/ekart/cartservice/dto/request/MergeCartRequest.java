package com.infy.ekart.cartservice.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record MergeCartRequest(
    @NotNull(message = "Guest cart ID is required")
    UUID guestCartId,
    
    @NotNull(message = "User cart ID is required")
    UUID userCartId,
    
    @NotNull(message = "User ID is required")
    UUID userId
) {}