package com.infy.ekart.cartservice.repository;

import com.infy.ekart.cartservice.entity.CartConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartConfigurationRepository extends JpaRepository<CartConfiguration, UUID> {

    Optional<CartConfiguration> findByUserId(UUID userId);

    Optional<CartConfiguration> findByCountryCode(String countryCode);
}