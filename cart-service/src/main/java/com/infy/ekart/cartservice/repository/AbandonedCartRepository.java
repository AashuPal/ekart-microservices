package com.infy.ekart.cartservice.repository;

import com.infy.ekart.cartservice.entity.AbandonedCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AbandonedCartRepository extends JpaRepository<AbandonedCart, UUID> {

    Optional<AbandonedCart> findByCartId(UUID cartId);

    List<AbandonedCart> findByStatusAndEmailSent(String status, Boolean emailSent);

    @Query("SELECT ac FROM AbandonedCart ac WHERE ac.status = 'PENDING' AND ac.createdAt < :cutoffDate")
    List<AbandonedCart> findAbandonedCartsForReminder(@Param("cutoffDate") LocalDateTime cutoffDate);

    List<AbandonedCart> findByUserIdAndStatus(UUID userId, String status);
}