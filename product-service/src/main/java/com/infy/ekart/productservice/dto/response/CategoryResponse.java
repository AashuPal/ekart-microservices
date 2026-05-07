package com.infy.ekart.productservice.dto.response;

import java.util.List;
import java.util.UUID;

public class CategoryResponse {

    private UUID id;
    private String name;
    private String slug;
    private String description;
    private String imageUrl;
    private UUID parentId;
    private List<CategoryResponse> subCategories;
    private Integer sortOrder;

    public CategoryResponse(UUID id, String name, String slug, String description,
                           String imageUrl, UUID parentId, List<CategoryResponse> subCategories,
                           Integer sortOrder) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.imageUrl = imageUrl;
        this.parentId = parentId;
        this.subCategories = subCategories;
        this.sortOrder = sortOrder;
    }

    // Getters
    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getSlug() { return slug; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public UUID getParentId() { return parentId; }
    public List<CategoryResponse> getSubCategories() { return subCategories; }
    public Integer getSortOrder() { return sortOrder; }
}