package com.infy.ekart.cartservice.repository;

import com.infy.ekart.cartservice.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, UUID> {

    List<CartItem> findByCartId(UUID cartId);

    Optional<CartItem> findByIdAndCartId(UUID itemId, UUID cartId);

    Optional<CartItem> findByCartIdAndSkuId(UUID cartId, UUID skuId);

    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.id = :cartId AND ci.productId = :productId")
    List<CartItem> findByCartIdAndProductId(@Param("cartId") UUID cartId, @Param("productId") UUID productId);

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.id = :cartId")
    void deleteAllByCartId(@Param("cartId") UUID cartId);

    @Query("SELECT COALESCE(SUM(ci.quantity), 0) FROM CartItem ci WHERE ci.cart.id = :cartId")
    Integer getTotalItemCount(@Param("cartId") UUID cartId);

    @Query("SELECT ci FROM CartItem ci JOIN FETCH ci.cart WHERE ci.cart.id IN :cartIds")
    List<CartItem> findItemsByCartIds(@Param("cartIds") List<UUID> cartIds);
}