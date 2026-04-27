package com.infy.ekart.cartservice.service.impl;

import com.infy.ekart.cartservice.client.PromotionServiceClient;
import com.infy.ekart.cartservice.client.PromotionServiceClient.PromotionValidationResult;
import com.infy.ekart.cartservice.dto.response.PromotionResponse;
import com.infy.ekart.cartservice.entity.Cart;
import com.infy.ekart.cartservice.entity.CartPromotion;
import com.infy.ekart.cartservice.enums.DiscountType;
import com.infy.ekart.cartservice.exception.CartNotFoundException;
import com.infy.ekart.cartservice.exception.PromotionNotApplicableException;
import com.infy.ekart.cartservice.mapper.CartMapper;
import com.infy.ekart.cartservice.repository.CartPromotionRepository;
import com.infy.ekart.cartservice.repository.CartRepository;
import com.infy.ekart.cartservice.service.CartPromotionService;
import com.infy.ekart.cartservice.service.CartPricingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartPromotionServiceImpl implements CartPromotionService {

    private static final Logger log = LoggerFactory.getLogger(CartPromotionServiceImpl.class);

    private final CartRepository cartRepository;
    private final CartPromotionRepository promotionRepository;
    private final PromotionServiceClient promotionClient;
    private final CartPricingService pricingService;
    private final CartMapper cartMapper;

    // Manual constructor
    public CartPromotionServiceImpl(CartRepository cartRepository,
                                     CartPromotionRepository promotionRepository,
                                     PromotionServiceClient promotionClient,
                                     CartPricingService pricingService,
                                     CartMapper cartMapper) {
        this.cartRepository = cartRepository;
        this.promotionRepository = promotionRepository;
        this.promotionClient = promotionClient;
        this.pricingService = pricingService;
        this.cartMapper = cartMapper;
    }

    @Override
    public PromotionResponse applyPromotion(String cartId, String couponCode) {
        log.info("Applying promotion {} to cart {}", couponCode, cartId);
        
        UUID cartUuid = UUID.fromString(cartId);
        Cart cart = cartRepository.findByIdWithItems(cartUuid)
            .orElseThrow(() -> new CartNotFoundException(cartUuid));

        var pricing = pricingService.calculatePricing(cart);

        PromotionValidationResult validation = promotionClient
            .validateCoupon(couponCode, pricing.subtotal());

        if (!validation.valid()) {
            throw new PromotionNotApplicableException(couponCode, validation.message());
        }

        // Create CartPromotion without builder
        CartPromotion promotion = new CartPromotion();
        promotion.setCart(cart);
        promotion.setCouponCode(couponCode);
        promotion.setDiscountAmount(validation.discountAmount());
        promotion.setDiscountType(DiscountType.valueOf(validation.discountType()));
        promotion.setStatus("ACTIVE");
        promotion.setAppliedAt(LocalDateTime.now());

        promotion = promotionRepository.save(promotion);
        cart.addPromotion(promotion);
        
        log.info("Promotion {} applied to cart {}", couponCode, cartId);
        
        return cartMapper.toPromotionResponse(promotion);
    }

    @Override
    public void removePromotion(String cartId, String promotionId) {
        log.info("Removing promotion {} from cart {}", promotionId, cartId);
        
        UUID promotionUuid = UUID.fromString(promotionId);
        promotionRepository.findById(promotionUuid).ifPresent(promotion -> {
            promotion.setStatus("INACTIVE");
            promotionRepository.save(promotion);
            log.info("Promotion {} removed", promotionId);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromotionResponse> getActivePromotions(String cartId) {
        UUID cartUuid = UUID.fromString(cartId);
        return promotionRepository.findActivePromotionsByCartId(cartUuid).stream()
            .map(cartMapper::toPromotionResponse)
            .collect(Collectors.toList());
    }
}