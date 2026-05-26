package com.infy.ekart.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShippingAddressResponse {
    private Long id;
    private String fullName;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String phoneNumber;
    private boolean isDefault;
} 