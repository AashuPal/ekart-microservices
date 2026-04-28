package com.infy.ekart.productservice.service;

import com.infy.ekart.productservice.dto.request.*;
import com.infy.ekart.productservice.dto.response.*;

public interface ProductService {

    ProductResponse createProduct(CreateProductRequest request);

    ProductResponse getProductById(String productId);

    ProductResponse updateProduct(String productId, UpdateProductRequest request);

    void deleteProduct(String productId);

    ProductListResponse getProducts(int page, int size, String category, String brand,
                                    String sortBy, String sortDir);

    ProductListResponse searchProducts(ProductSearchRequest request);

    StockResponse getStock(String productId);

    StockResponse updateStock(String productId, StockUpdateRequest request);
}