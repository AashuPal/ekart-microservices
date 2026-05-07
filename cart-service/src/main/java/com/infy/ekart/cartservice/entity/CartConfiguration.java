package com.infy.ekart.cartservice.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "cart_configuration")
public class CartConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", unique = true)
    private UUID userId;

    @Column(name = "max_items_per_cart")
    private Integer maxItemsPerCart = 50;

    @Column(name = "cart_expiry_hours")
    private Integer cartExpiryHours = 24;

    @Column(name = "reservation_timeout_min")
    private Integer reservationTimeoutMin = 15;

    @Column(name = "allowed_payment_methods", columnDefinition = "JSON")  // Changed from JSONB
    private String allowedPaymentMethods;

    @Column(name = "country_code", length = 3)
    private String countryCode = "IN";

    @Column(name = "currency_code", length = 3)
    private String currencyCode = "INR";

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public Integer getMaxItemsPerCart() { return maxItemsPerCart; }
    public void setMaxItemsPerCart(Integer maxItemsPerCart) { this.maxItemsPerCart = maxItemsPerCart; }

    public Integer getCartExpiryHours() { return cartExpiryHours; }
    public void setCartExpiryHours(Integer cartExpiryHours) { this.cartExpiryHours = cartExpiryHours; }

    public Integer getReservationTimeoutMin() { return reservationTimeoutMin; }
    public void setReservationTimeoutMin(Integer reservationTimeoutMin) { this.reservationTimeoutMin = reservationTimeoutMin; }

    public String getAllowedPaymentMethods() { return allowedPaymentMethods; }
    public void setAllowedPaymentMethods(String allowedPaymentMethods) { this.allowedPaymentMethods = allowedPaymentMethods; }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public String getCurrencyCode() { return currencyCode; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }
}