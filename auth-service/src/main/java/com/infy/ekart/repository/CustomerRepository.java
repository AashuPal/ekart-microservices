package com.infy.ekart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.infy.ekart.entity.Customer;


public interface CustomerRepository extends JpaRepository<Customer, String> {
	boolean existsByEmailId(String emailId);
}