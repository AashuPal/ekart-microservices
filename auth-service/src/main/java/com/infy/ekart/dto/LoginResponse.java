package com.infy.ekart.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LoginResponse(
    String token,
    String email,
    String name,
    String phoneNumber,
    String role,
    long expiresIn
) {}