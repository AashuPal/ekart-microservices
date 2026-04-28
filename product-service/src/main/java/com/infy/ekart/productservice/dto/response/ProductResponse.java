package com.infy.ekart.productservice.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ProductResponse {

    private UUID id;
    private String name;
    private String description;
    private String slug;
    private String sku;
    private BigDecimal basePrice;
    private BigDecimal sellingPrice;
    private Integer discountPercentage;
    private String categoryName;
    private String brandName;
    private String status;
    private String thumbnailUrl;
    private Double averageRating;
    private Integer reviewCount;
    private List<String> images;
    private StockResponse stock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor
    public ProductResponse(UUID id, String name, String description, String slug, String sku,
                          BigDecimal basePrice, BigDecimal sellingPrice, Integer discountPercentage,
                          String categoryName, String brandName, String status, String thumbnailUrl,
                          Double averageRating, Integer reviewCount, List<String> images,
                          StockResponse stock, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.slug = slug;
        this.sku = sku;
        this.basePrice = basePrice;
        this.sellingPrice = sellingPrice;
        this.discountPercentage = discountPercentage;
        this.categoryName = categoryName;
        this.brandName = brandName;
        this.status = status;
        this.thumbnailUrl = thumbnailUrl;
        this.averageRating = averageRating;
        this.reviewCount = reviewCount;
        this.images = images;
        this.stock = stock;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters
    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getSlug() { return slug; }
    public String getSku() { return sku; }
    public BigDecimal getBasePrice() { return basePrice; }
    public BigDecimal getSellingPrice() { return sellingPrice; }
    public Integer getDiscountPercentage() { return discountPercentage; }
    public String getCategoryName() { return categoryName; }
    public String getBrandName() { return brandName; }
    public String getStatus() { return status; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public Double getAverageRating() { return averageRating; }
    public Integer getReviewCount() { return reviewCount; }
    public List<String> getImages() { return images; }
    public StockResponse getStock() { return stock; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}