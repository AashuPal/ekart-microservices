package com.infy.ekart.cartservice.service.impl;

import com.infy.ekart.cartservice.dto.response.PricingResponse;
import com.infy.ekart.cartservice.entity.Cart;
import com.infy.ekart.cartservice.exception.CartNotFoundException;
import com.infy.ekart.cartservice.repository.CartPromotionRepository;
import com.infy.ekart.cartservice.repository.CartRepository;
import com.infy.ekart.cartservice.service.CartPricingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class CartPricingServiceImpl implements CartPricingService {

    private static final Logger log = LoggerFactory.getLogger(CartPricingServiceImpl.class);

    private final CartRepository cartRepository;
    private final CartPromotionRepository cartPromotionRepository;

    // FIX: Properly assign fields in constructor
    public CartPricingServiceImpl(CartRepository cartRepository,
                                   CartPromotionRepository cartPromotionRepository) {
        this.cartRepository = cartRepository;                    // THIS WAS MISSING!
        this.cartPromotionRepository = cartPromotionRepository;  // THIS WAS MISSING!
    }

    @Override
    public PricingResponse calculatePricing(Cart cart) {
        BigDecimal subtotal = cart.getItems().stream()
            .map(item -> item.getTotalPrice())
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discount = cartPromotionRepository.getTotalDiscountByCartId(cart.getId());
        BigDecimal tax = subtotal.multiply(new BigDecimal("0.18"));
        BigDecimal shipping = calculateShipping(subtotal);
        BigDecimal totalAmount = subtotal.subtract(discount).add(tax).add(shipping);

        return new PricingResponse(subtotal, discount, tax, shipping, totalAmount, "INR");
    }

    @Override
    public PricingResponse calculatePricing(String cartId) {
        UUID id = UUID.fromString(cartId);
        Cart cart = cartRepository.findByIdWithItems(id)
            .orElseThrow(() -> new CartNotFoundException(id));
        
        return calculatePricing(cart);
    }

    private BigDecimal calculateShipping(BigDecimal subtotal) {
        if (subtotal.compareTo(new BigDecimal("500")) >= 0) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal("50");
    }
}