package com.infy.ekart.productservice.service.impl;

import com.infy.ekart.productservice.dto.request.StockUpdateRequest;
import com.infy.ekart.productservice.dto.response.StockResponse;
import com.infy.ekart.productservice.entity.ProductInventory;
import com.infy.ekart.productservice.exception.InsufficientStockException;
import com.infy.ekart.productservice.repository.ProductInventoryRepository;
import com.infy.ekart.productservice.service.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryServiceImpl.class);
    private final ProductInventoryRepository inventoryRepository;

    public InventoryServiceImpl(ProductInventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public StockResponse getStock(String sku) {
        ProductInventory inventory = inventoryRepository.findBySku(sku)
            .orElseThrow(() -> new RuntimeException("Inventory not found"));
        return toStockResponse(inventory);
    }

    @Override
    public StockResponse updateStock(String sku, StockUpdateRequest request) {
        ProductInventory inventory = inventoryRepository.findBySku(sku)
            .orElseThrow(() -> new RuntimeException("Inventory not found"));
        inventory.setQuantity(request.getQuantity());
        inventory = inventoryRepository.save(inventory);
        return toStockResponse(inventory);
    }

    @Override
    public boolean checkAvailability(String sku, int quantity) {
        ProductInventory inventory = inventoryRepository.findBySku(sku)
            .orElseThrow(() -> new RuntimeException("Inventory not found"));
        return inventory.getAvailableQuantity() >= quantity;
    }

    @Override
    public void reserveStock(String sku, int quantity) {
        ProductInventory inventory = inventoryRepository.findBySku(sku)
            .orElseThrow(() -> new RuntimeException("Inventory not found"));
        if (inventory.getAvailableQuantity() < quantity) {
            throw new InsufficientStockException(sku, quantity, inventory.getAvailableQuantity());
        }
        inventory.setReservedQuantity(inventory.getReservedQuantity() + quantity);
        inventoryRepository.save(inventory);
    }

    @Override
    public void releaseStock(String sku, int quantity) {
        ProductInventory inventory = inventoryRepository.findBySku(sku)
            .orElseThrow(() -> new RuntimeException("Inventory not found"));
        inventory.setReservedQuantity(inventory.getReservedQuantity() - quantity);
        inventoryRepository.save(inventory);
    }

    private StockResponse toStockResponse(ProductInventory inventory) {
        return new StockResponse(
            inventory.getId(),
            inventory.getProduct().getId(),
            inventory.getQuantity(),
            inventory.getAvailableQuantity(),
            inventory.getStockStatus().toString()
        );
    }
}