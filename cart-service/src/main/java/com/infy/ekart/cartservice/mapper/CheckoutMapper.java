package com.infy.ekart.cartservice.mapper;

import com.infy.ekart.cartservice.dto.response.CheckoutResponse;
import com.infy.ekart.cartservice.entity.Cart;
import com.infy.ekart.cartservice.entity.CartPromotion;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class CheckoutMapper {

    public CheckoutResponse toCheckoutResponse(
            Cart cart,
            String orderId,
            String orderNumber,
            BigDecimal subtotal,
            BigDecimal discount,
            BigDecimal tax,
            BigDecimal shipping,
            BigDecimal totalAmount) {

        return new CheckoutResponse(
            cart.getId(),
            orderId != null ? UUID.fromString(orderId) : null,
            orderNumber,
            subtotal,
            discount,
            tax,
            shipping,
            totalAmount,
            "INR",
            "SUCCESS",
            LocalDateTime.now()
        );
    }

    public BigDecimal calculateSubtotal(Cart cart) {
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        return cart.getItems().stream()
            .map(item -> item.getTotalPrice())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calculateDiscount(List<CartPromotion> promotions) {
        if (promotions == null || promotions.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        return promotions.stream()
            .filter(p -> "ACTIVE".equals(p.getStatus()))
            .map(CartPromotion::getDiscountAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}