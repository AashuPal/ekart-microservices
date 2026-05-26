package com.infy.ekart.repository;

import com.infy.ekart.entity.ShippingAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ShippingAddressRepository extends JpaRepository<ShippingAddress, Long> {
    List<ShippingAddress> findByCustomerEmail(String customerEmail);
    void deleteByIdAndCustomerEmail(Long id, String customerEmail);
}