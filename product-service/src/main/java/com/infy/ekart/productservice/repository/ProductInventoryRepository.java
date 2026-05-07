package com.infy.ekart.productservice.repository;

import com.infy.ekart.productservice.entity.ProductInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductInventoryRepository extends JpaRepository<ProductInventory, UUID> {

    Optional<ProductInventory> findByProductId(UUID productId);

    Optional<ProductInventory> findBySku(String sku);
}