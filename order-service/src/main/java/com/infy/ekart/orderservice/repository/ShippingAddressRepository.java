package com.infy.ekart.orderservice.repository;

import com.infy.ekart.orderservice.entity.ShippingAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShippingAddressRepository extends JpaRepository<ShippingAddress, UUID> {
    List<ShippingAddress> findByCustomerEmail(String customerEmail);
    void deleteByIdAndCustomerEmail(UUID id, String customerEmail);
    Optional<ShippingAddress> findByIdAndCustomerEmail(UUID id, String customerEmail);
}