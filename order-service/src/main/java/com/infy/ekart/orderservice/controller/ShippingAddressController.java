package com.infy.ekart.orderservice.controller;

import com.infy.ekart.orderservice.entity.ShippingAddress;
import com.infy.ekart.orderservice.repository.ShippingAddressRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/addresses")
public class ShippingAddressController {

    private final ShippingAddressRepository addressRepository;

    public ShippingAddressController(ShippingAddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    // GET all addresses for the current user (email from gateway header)
    @GetMapping
    public ResponseEntity<List<ShippingAddress>> getUserAddresses(
            @RequestHeader("X-User-Email") String userEmail) {
        List<ShippingAddress> addresses = addressRepository.findByCustomerEmail(userEmail);
        return ResponseEntity.ok(addresses);
    }

    // POST a new address
    @PostMapping
    public ResponseEntity<ShippingAddress> createAddress(
            @RequestHeader("X-User-Email") String userEmail,
            @RequestBody ShippingAddress address) {
        address.setCustomerEmail(userEmail);
        ShippingAddress saved = addressRepository.save(address);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT update an address
    @PutMapping("/{id}")
    public ResponseEntity<ShippingAddress> updateAddress(
            @PathVariable UUID id,
            @RequestHeader("X-User-Email") String userEmail,
            @RequestBody ShippingAddress updated) {
        ShippingAddress existing = addressRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Address not found"));
        if (!existing.getCustomerEmail().equals(userEmail)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        existing.setFullName(updated.getFullName());
        existing.setAddressLine1(updated.getAddressLine1());
        existing.setAddressLine2(updated.getAddressLine2());
        existing.setCity(updated.getCity());
        existing.setState(updated.getState());
        existing.setPostalCode(updated.getPostalCode());
        existing.setCountry(updated.getCountry());
        existing.setPhoneNumber(updated.getPhoneNumber());
        existing.setIsDefault(updated.getIsDefault());
        return ResponseEntity.ok(addressRepository.save(existing));
    }

    // DELETE an address
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAddress(@PathVariable UUID id,
                                         @RequestHeader("X-User-Email") String userEmail) {
        ShippingAddress address = addressRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Address not found"));
        if (!address.getCustomerEmail().equals(userEmail)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        addressRepository.delete(address);
        return ResponseEntity.noContent().build();
    }

    // PATCH set default
    @PatchMapping("/{id}/default")
    public ResponseEntity<ShippingAddress> setDefaultAddress(@PathVariable UUID id,
                                                           @RequestHeader("X-User-Email") String userEmail) {
        // Reset all defaults for this user
        List<ShippingAddress> userAddresses = addressRepository.findByCustomerEmail(userEmail);
        userAddresses.forEach(addr -> addr.setIsDefault(false));
        addressRepository.saveAll(userAddresses);

        ShippingAddress address = addressRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Address not found"));
        if (!address.getCustomerEmail().equals(userEmail)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        address.setIsDefault(true);
        return ResponseEntity.ok(addressRepository.save(address));
    }
}