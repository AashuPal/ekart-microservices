package com.infy.ekart.cartservice.repository;

import com.infy.ekart.cartservice.entity.CartPromotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartPromotionRepository extends JpaRepository<CartPromotion, UUID> {

    List<CartPromotion> findByCartId(UUID cartId);

    Optional<CartPromotion> findByCartIdAndCouponCode(UUID cartId, String couponCode);

    @Query("SELECT cp FROM CartPromotion cp WHERE cp.cart.id = :cartId AND cp.status = 'ACTIVE'")
    List<CartPromotion> findActivePromotionsByCartId(@Param("cartId") UUID cartId);

    @Query("SELECT COALESCE(SUM(cp.discountAmount), 0) FROM CartPromotion cp WHERE cp.cart.id = :cartId AND cp.status = 'ACTIVE'")
    java.math.BigDecimal getTotalDiscountByCartId(@Param("cartId") UUID cartId);
}