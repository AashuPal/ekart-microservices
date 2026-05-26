package com.infy.ekart.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ShippingAddressRequest {
    @NotBlank
    private String fullName;

    @NotBlank
    private String addressLine1;

    private String addressLine2;

    @NotBlank
    private String city;

    @NotBlank
    private String state;

    @NotBlank
    private String postalCode;

    @NotBlank
    private String country;

    private String phoneNumber;

    private boolean isDefault;
}