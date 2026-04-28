package com.infy.ekart.productservice.controller;

import com.infy.ekart.productservice.dto.response.BrandResponse;
import com.infy.ekart.productservice.service.BrandService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/brands")
public class BrandController {

    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping
    public ResponseEntity<List<BrandResponse>> getAllBrands() {
        List<BrandResponse> brands = brandService.getAllBrands();
        return ResponseEntity.ok(brands);
    }

    @GetMapping("/{brandId}")
    public ResponseEntity<BrandResponse> getBrand(@PathVariable String brandId) {
        BrandResponse response = brandService.getBrandById(brandId);
        return ResponseEntity.ok(response);
    }
}