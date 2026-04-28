package com.infy.ekart.productservice.service;

import com.infy.ekart.productservice.dto.response.BrandResponse;

import java.util.List;

public interface BrandService {
    List<BrandResponse> getAllBrands();
    BrandResponse getBrandById(String brandId);
}