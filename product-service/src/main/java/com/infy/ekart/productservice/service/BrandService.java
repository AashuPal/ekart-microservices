package com.infy.ekart.productservice.service;

import com.infy.ekart.productservice.dto.response.BrandResponse;

import java.util.List;

public interface BrandService {
	BrandResponse createBrand(Object request);
    List<BrandResponse> getAllBrands();
    BrandResponse getBrandById(String brandId);
}