package com.infy.ekart.cartservice.repository;

import com.infy.ekart.cartservice.entity.ProductReservation;
import com.infy.ekart.cartservice.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductReservationRepository extends JpaRepository<ProductReservation, UUID> {

    Optional<ProductReservation> findByCartItemId(UUID cartItemId);

    List<ProductReservation> findByStatusAndReservationExpiryBefore(
            ReservationStatus status, LocalDateTime dateTime);

    @Query("SELECT pr FROM ProductReservation pr WHERE pr.cartItem.cart.id = :cartId")
    List<ProductReservation> findByCartId(@Param("cartId") UUID cartId);

    @Modifying
    @Query("UPDATE ProductReservation pr SET pr.status = :status WHERE pr.cartItem.cart.id = :cartId")
    void updateStatusByCartId(@Param("cartId") UUID cartId, @Param("status") ReservationStatus status);
}