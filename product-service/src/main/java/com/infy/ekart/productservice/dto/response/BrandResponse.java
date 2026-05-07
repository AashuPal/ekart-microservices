package com.infy.ekart.productservice.dto.response;

import java.util.UUID;

public class BrandResponse {
    private UUID id;
    private String name;
    private String slug;
    private String logoUrl;
    private String description;

    public BrandResponse(UUID id, String name, String slug, String logoUrl, String description) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.logoUrl = logoUrl;
        this.description = description;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getSlug() { return slug; }
    public String getLogoUrl() { return logoUrl; }
    public String getDescription() { return description; }
}