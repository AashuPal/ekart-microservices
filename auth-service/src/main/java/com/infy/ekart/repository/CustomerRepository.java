package com.infy.ekart.repository;

import com.infy.ekart.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {

    Optional<Customer> findByEmailId(String emailId);
    
    Optional<Customer> findByVerifiedEmail(String verifiedEmail);

    boolean existsByEmailId(String emailId);
    
    Optional<Customer> findByPhoneNumber(String phoneNumber);
 
    Optional<Customer> findByPhoneNumberAndProvider(String phoneNumber, String provider);
}