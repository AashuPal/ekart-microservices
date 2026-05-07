package com.infy.ekart.cartservice.mapper;

import com.infy.ekart.cartservice.dto.response.CartResponse;
import com.infy.ekart.cartservice.dto.response.CartItemResponse;
import com.infy.ekart.cartservice.dto.response.PromotionResponse;
import com.infy.ekart.cartservice.entity.Cart;
import com.infy.ekart.cartservice.entity.CartItem;
import com.infy.ekart.cartservice.entity.CartPromotion;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CartMapper {

    private final CartItemMapper cartItemMapper;

    public CartMapper(CartItemMapper cartItemMapper) {
        this.cartItemMapper = cartItemMapper;
    }

    public CartResponse toCartResponse(Cart cart) {
        if (cart == null) return null;

        List<CartItemResponse> itemResponses = cart.getItems().stream()
            .map(cartItemMapper::toCartItemResponse)
            .collect(Collectors.toList());

        List<PromotionResponse> promotionResponses = cart.getPromotions().stream()
            .map(this::toPromotionResponse)
            .collect(Collectors.toList());

        BigDecimal subtotal = calculateSubtotal(cart.getItems());
        BigDecimal discount = calculateTotalDiscount(cart.getPromotions());

        return new CartResponse(
            cart.getId(),
            cart.getUserId(),
            cart.getSessionId(),
            cart.getStatus(),
            getTotalItems(cart),
            subtotal,
            discount,
            subtotal.subtract(discount),
            "INR",
            itemResponses,
            promotionResponses,
            cart.getCreatedAt(),
            cart.getUpdatedAt(),
            cart.getExpiresAt()
        );
    }

    public PromotionResponse toPromotionResponse(CartPromotion promotion) {
        if (promotion == null) return null;

        return new PromotionResponse(
            promotion.getId(),
            promotion.getCouponCode(),
            promotion.getDiscountType(),
            promotion.getDiscountAmount(),
            promotion.getStatus()
        );
    }

    // ✅ Changed from List to Collection (works with both List and Set)
    private BigDecimal calculateSubtotal(Collection<CartItem> items) {
        if (items == null || items.isEmpty()) return BigDecimal.ZERO;
        
        return items.stream()
            .map(CartItem::getTotalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // ✅ Changed from List to Collection
    private BigDecimal calculateTotalDiscount(Collection<CartPromotion> promotions) {
        if (promotions == null || promotions.isEmpty()) return BigDecimal.ZERO;
        
        return promotions.stream()
            .filter(p -> "ACTIVE".equals(p.getStatus()))
            .map(CartPromotion::getDiscountAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // ✅ Works with Set
    private int getTotalItems(Cart cart) {
        if (cart.getItems() == null || cart.getItems().isEmpty()) return 0;
        
        return cart.getItems().stream()
            .mapToInt(CartItem::getQuantity)
            .sum();
    }
}