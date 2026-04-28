package com.infy.ekart.productservice.service.impl;

import com.infy.ekart.productservice.dto.response.BrandResponse;
import com.infy.ekart.productservice.entity.Brand;
import com.infy.ekart.productservice.repository.BrandRepository;
import com.infy.ekart.productservice.service.BrandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
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
    public List<BrandResponse> getAllBrands() {
        return brandRepository.findAll().stream()
            .map(this::toBrandResponse)
            .collect(Collectors.toList());
    }

    @Override
    public BrandResponse getBrandById(String brandId) {
        UUID id = UUID.fromString(brandId);
        Brand brand = brandRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Brand not found"));
        return toBrandResponse(brand);
    }

    private BrandResponse toBrandResponse(Brand brand) {
        return new BrandResponse(
            brand.getId(),
            brand.getName(),
            brand.getSlug(),
            brand.getLogoUrl(),
            brand.getDescription()
        );
    }
}