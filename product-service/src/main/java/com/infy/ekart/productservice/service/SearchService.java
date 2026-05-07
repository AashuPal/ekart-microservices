package com.infy.ekart.productservice.service;

import com.infy.ekart.productservice.dto.request.ProductSearchRequest;
import com.infy.ekart.productservice.dto.response.ProductListResponse;

public interface SearchService {
    ProductListResponse search(ProductSearchRequest request);
}