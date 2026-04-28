package com.infy.ekart.productservice.service;

import com.infy.ekart.productservice.dto.request.StockUpdateRequest;
import com.infy.ekart.productservice.dto.response.StockResponse;

public interface InventoryService {
    StockResponse getStock(String sku);
    StockResponse updateStock(String sku, StockUpdateRequest request);
    boolean checkAvailability(String sku, int quantity);
    void reserveStock(String sku, int quantity);
    void releaseStock(String sku, int quantity);
}