package com.infy.ekart.service;

import com.infy.ekart.dto.*;
import com.infy.ekart.entity.ShippingAddress;
import com.infy.ekart.repository.ShippingAddressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ShippingAddressService {

    private final ShippingAddressRepository repository;

    public ShippingAddressService(ShippingAddressRepository repository) {
        this.repository = repository;
    }

    public List<ShippingAddressResponse> getAllAddresses(String customerEmail) {
        return repository.findByCustomerEmail(customerEmail)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ShippingAddressResponse addAddress(String customerEmail,
                                              ShippingAddressRequest request) {
        ShippingAddress address = ShippingAddress.builder()
                .customerEmail(customerEmail)
                .fullName(request.getFullName())
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .city(request.getCity())
                .state(request.getState())
                .postalCode(request.getPostalCode())
                .country(request.getCountry())
                .phoneNumber(request.getPhoneNumber())
                .isDefault(request.isDefault())
                .build();

        if (address.isDefault()) {
            unsetExistingDefaults(customerEmail);
        }

        return toResponse(repository.save(address));
    }

    public ShippingAddressResponse updateAddress(String customerEmail,
                                                 Long addressId,
                                                 ShippingAddressRequest request) {
        ShippingAddress address = repository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        if (!address.getCustomerEmail().equals(customerEmail)) {
            throw new RuntimeException("Access denied");
        }

        address.setFullName(request.getFullName());
        address.setAddressLine1(request.getAddressLine1());
        address.setAddressLine2(request.getAddressLine2());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPostalCode(request.getPostalCode());
        address.setCountry(request.getCountry());
        address.setPhoneNumber(request.getPhoneNumber());

        if (request.isDefault()) {
            unsetExistingDefaults(customerEmail);
            address.setDefault(true);
        }

        return toResponse(repository.save(address));
    }

    public ApiResponse deleteAddress(String customerEmail, Long addressId) {
        repository.deleteByIdAndCustomerEmail(addressId, customerEmail);
        return new ApiResponse("Address deleted", true);
    }

    public ShippingAddressResponse setDefault(String customerEmail, Long addressId) {
        ShippingAddress address = repository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        if (!address.getCustomerEmail().equals(customerEmail)) {
            throw new RuntimeException("Access denied");
        }
        unsetExistingDefaults(customerEmail);
        address.setDefault(true);
        repository.save(address);
        return toResponse(address);
    }

    private void unsetExistingDefaults(String customerEmail) {
        List<ShippingAddress> all = repository.findByCustomerEmail(customerEmail);
        all.forEach(addr -> addr.setDefault(false));
        repository.saveAll(all);
    }

    private ShippingAddressResponse toResponse(ShippingAddress address) {
        return ShippingAddressResponse.builder()
                .id(address.getId())
                .fullName(address.getFullName())
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .city(address.getCity())
                .state(address.getState())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .phoneNumber(address.getPhoneNumber())
                .isDefault(address.isDefault())
                .build();
    }
}