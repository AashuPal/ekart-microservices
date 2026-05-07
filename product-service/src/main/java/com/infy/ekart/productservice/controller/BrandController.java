package com.infy.ekart.productservice.controller;

import com.infy.ekart.productservice.service.BrandService;
import com.infy.ekart.productservice.dto.response.BrandResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/brands")
public class BrandController {

    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<BrandResponse> createBrand(@RequestBody Map<String, Object> request) {
        BrandResponse response = brandService.createBrand(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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