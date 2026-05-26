package com.infy.ekart.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "shipping_address")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_email", nullable = false)
    private String customerEmail;     // from JWT / gateway

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String addressLine1;

    private String addressLine2;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String postalCode;

    @Column(nullable = false)
    private String country;

    private String phoneNumber;

    private boolean isDefault;
}