package com.infy.ekart.controller;

import com.infy.ekart.dto.*;
import com.infy.ekart.service.ShippingAddressService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class ShippingAddressController {

    private final ShippingAddressService service;

    public ShippingAddressController(ShippingAddressService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<ShippingAddressResponse>> getAll(
            @RequestHeader("X-User-Email") String email) {
        return ResponseEntity.ok(service.getAllAddresses(email));
    }

    @PostMapping
    public ResponseEntity<ShippingAddressResponse> add(
            @RequestHeader("X-User-Email") String email,
            @Valid @RequestBody ShippingAddressRequest request) {
        return ResponseEntity.ok(service.addAddress(email, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShippingAddressResponse> update(
            @RequestHeader("X-User-Email") String email,
            @PathVariable Long id,
            @Valid @RequestBody ShippingAddressRequest request) {
        return ResponseEntity.ok(service.updateAddress(email, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(
            @RequestHeader("X-User-Email") String email,
            @PathVariable Long id) {
        return ResponseEntity.ok(service.deleteAddress(email, id));
    }

    @PatchMapping("/{id}/default")
    public ResponseEntity<ShippingAddressResponse> setDefault(
            @RequestHeader("X-User-Email") String email,
            @PathVariable Long id) {
        return ResponseEntity.ok(service.setDefault(email, id));
    }
}