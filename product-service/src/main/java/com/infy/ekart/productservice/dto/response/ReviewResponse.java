package com.infy.ekart.productservice.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public class ReviewResponse {

    private UUID id;
    private UUID productId;
    private UUID userId;
    private String userName;
    private Integer rating;
    private String title;
    private String comment;
    private Boolean isVerifiedPurchase;
    private Integer helpfulCount;
    private LocalDateTime createdAt;

    public ReviewResponse(UUID id, UUID productId, UUID userId, String userName,
                         Integer rating, String title, String comment,
                         Boolean isVerifiedPurchase, Integer helpfulCount, LocalDateTime createdAt) {
        this.id = id;
        this.productId = productId;
        this.userId = userId;
        this.userName = userName;
        this.rating = rating;
        this.title = title;
        this.comment = comment;
        this.isVerifiedPurchase = isVerifiedPurchase;
        this.helpfulCount = helpfulCount;
        this.createdAt = createdAt;
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getProductId() { return productId; }
    public UUID getUserId() { return userId; }
    public String getUserName() { return userName; }
    public Integer getRating() { return rating; }
    public String getTitle() { return title; }
    public String getComment() { return comment; }
    public Boolean getIsVerifiedPurchase() { return isVerifiedPurchase; }
    public Integer getHelpfulCount() { return helpfulCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}