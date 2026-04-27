package com.infy.ekart.cartservice.repository;

import com.infy.ekart.cartservice.entity.Cart;
import com.infy.ekart.cartservice.enums.CartStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {

    Optional<Cart> findByIdAndStatus(UUID cartId, CartStatus status);

    Optional<Cart> findByUserIdAndStatus(UUID userId, CartStatus status);

    Optional<Cart> findBySessionIdAndStatus(UUID sessionId, CartStatus status);

    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.items WHERE c.id = :cartId")
    Optional<Cart> findByIdWithItems(@Param("cartId") UUID cartId);

    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.items LEFT JOIN FETCH c.promotions WHERE c.id = :cartId")
    Optional<Cart> findByIdWithItemsAndPromotions(@Param("cartId") UUID cartId);

    List<Cart> findByStatusAndExpiresAtBefore(CartStatus status, LocalDateTime dateTime);

    @Query("SELECT c FROM Cart c WHERE c.userId = :userId AND c.status = 'ACTIVE' ORDER BY c.updatedAt DESC")
    List<Cart> findActiveCartsByUserId(@Param("userId") UUID userId);

    @Query("SELECT COUNT(c) FROM Cart c WHERE c.userId = :userId AND c.status = 'ACTIVE'")
    long countActiveCartsByUserId(@Param("userId") UUID userId);
}