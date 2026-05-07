package com.infy.ekart.paymentservice.repository;

import com.infy.ekart.paymentservice.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findTopByOrderIdOrderByCreatedAtDesc(UUID orderId);

    Optional<Payment> findByTransactionId(String transactionId);

    Optional<Payment> findByPaymentNumber(String paymentNumber);

    List<Payment> findByUserIdOrderByCreatedAtDesc(UUID userId);
}