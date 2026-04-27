package com.infy.ekart.cartservice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "abandoned_cart")
public class AbandonedCart {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "cart_id", nullable = false, unique = true)
    private UUID cartId;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "email_sent")
    private Boolean emailSent = false;

    @Column(name = "last_reminder_at")
    private LocalDateTime lastReminderAt;

    @Column(name = "recovery_url")
    private String recoveryUrl;

    @Column(name = "status", length = 20)
    private String status = "PENDING";

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // No-args constructor
    public AbandonedCart() {}

    // All-args constructor
    public AbandonedCart(UUID cartId, UUID userId, Boolean emailSent, 
                         String status, String recoveryUrl, LocalDateTime createdAt) {
        this.cartId = cartId;
        this.userId = userId;
        this.emailSent = emailSent;
        this.status = status;
        this.recoveryUrl = recoveryUrl;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getCartId() { return cartId; }
    public void setCartId(UUID cartId) { this.cartId = cartId; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public Boolean getEmailSent() { return emailSent; }
    public void setEmailSent(Boolean emailSent) { this.emailSent = emailSent; }

    public LocalDateTime getLastReminderAt() { return lastReminderAt; }
    public void setLastReminderAt(LocalDateTime lastReminderAt) { this.lastReminderAt = lastReminderAt; }

    public String getRecoveryUrl() { return recoveryUrl; }
    public void setRecoveryUrl(String recoveryUrl) { this.recoveryUrl = recoveryUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}