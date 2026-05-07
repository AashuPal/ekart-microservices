package com.infy.ekart.productservice.service.impl;

import com.infy.ekart.productservice.dto.response.BrandResponse;
import com.infy.ekart.productservice.entity.Brand;
import com.infy.ekart.productservice.repository.BrandRepository;
import com.infy.ekart.productservice.service.BrandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BrandServiceImpl implements BrandService {

    private static final Logger log = LoggerFactory.getLogger(BrandServiceImpl.class);
    private final BrandRepository brandRepository;

    public BrandServiceImpl(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    @Override
    @SuppressWarnings("unchecked")
    public BrandResponse createBrand(Object requestObj) {
        String name = null;
        String description = null;
        String logoUrl = null;

        // Extract values from Map
        if (requestObj instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) requestObj;
            name = (String) map.get("name");
            description = (String) map.get("description");
            logoUrl = (String) map.get("logoUrl");
        }

        // Validate name
        if (name == null || name.trim().isEmpty()) {
            throw new RuntimeException("Brand name is required");
        }

        // Create brand
        Brand brand = new Brand();
        brand.setName(name.trim());
        brand.setDescription(description);
        brand.setLogoUrl(logoUrl);
        brand.setIsActive(true);
        brand.setSlug(name.trim().toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", ""));

        // Save
        brand = brandRepository.save(brand);
        log.info("Brand created: {}", brand.getName());

        return new BrandResponse(
            brand.getId(),
            brand.getName(),
            brand.getSlug(),
            brand.getLogoUrl(),
            brand.getDescription()
        );
    }

    @Override
    public List<BrandResponse> getAllBrands() {
        return brandRepository.findAll().stream()
            .map(b -> new BrandResponse(b.getId(), b.getName(), b.getSlug(), b.getLogoUrl(), b.getDescription()))
            .collect(Collectors.toList());
    }

    @Override
    public BrandResponse getBrandById(String brandId) {
        UUID id = UUID.fromString(brandId);
        Brand b = brandRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Brand not found"));
        return new BrandResponse(b.getId(), b.getName(), b.getSlug(), b.getLogoUrl(), b.getDescription());
    }
}