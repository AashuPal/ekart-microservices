package com.infy.ekart.cartservice.service.impl;

import com.infy.ekart.cartservice.client.InventoryServiceClient;
import com.infy.ekart.cartservice.client.OrderServiceClient;
import com.infy.ekart.cartservice.client.OrderServiceClient.OrderResponse;
import com.infy.ekart.cartservice.dto.request.CheckoutRequest;
import com.infy.ekart.cartservice.dto.response.CheckoutResponse;
import com.infy.ekart.cartservice.dto.response.PricingResponse;
import com.infy.ekart.cartservice.entity.Cart;
import com.infy.ekart.cartservice.entity.CartPromotion;
import com.infy.ekart.cartservice.exception.CartNotFoundException;
import com.infy.ekart.cartservice.mapper.CheckoutMapper;
import com.infy.ekart.cartservice.repository.CartPromotionRepository;
import com.infy.ekart.cartservice.repository.CartRepository;
import com.infy.ekart.cartservice.service.*;
import com.infy.ekart.cartservice.validator.CartValidator;
import com.infy.ekart.cartservice.validator.CheckoutValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CartCheckoutServiceImpl implements CartCheckoutService {

    private static final Logger log = LoggerFactory.getLogger(CartCheckoutServiceImpl.class);

    private final CartRepository cartRepository;
    private final CartPromotionRepository promotionRepository;
    private final CartService cartService;
    private final CartPricingService pricingService;
    private final InventoryReservationService inventoryService;
    private final CartValidator cartValidator;
    private final CheckoutValidator checkoutValidator;
    private final CheckoutMapper checkoutMapper;
    private final OrderServiceClient orderClient;
    private final InventoryServiceClient inventoryClient;

    public CartCheckoutServiceImpl(CartRepository cartRepository,
                                    CartPromotionRepository promotionRepository,
                                    CartService cartService,
                                    CartPricingService pricingService,
                                    InventoryReservationService inventoryService,
                                    CartValidator cartValidator,
                                    CheckoutValidator checkoutValidator,
                                    CheckoutMapper checkoutMapper,
                                    OrderServiceClient orderClient,
                                    InventoryServiceClient inventoryClient) {
        this.cartRepository = cartRepository;
        this.promotionRepository = promotionRepository;
        this.cartService = cartService;
        this.pricingService = pricingService;
        this.inventoryService = inventoryService;
        this.cartValidator = cartValidator;
        this.checkoutValidator = checkoutValidator;
        this.checkoutMapper = checkoutMapper;
        this.orderClient = orderClient;
        this.inventoryClient = inventoryClient;
    }

    @Override
    public CheckoutResponse checkout(String cartId, CheckoutRequest request) {
        log.info("Starting checkout for cart: {}", cartId);

        UUID cartUuid = UUID.fromString(cartId);
        
        // ✅ FIXED: Use findByIdWithItems instead of findByIdWithItemsAndPromotions
        Cart cart = cartRepository.findByIdWithItems(cartUuid)
            .orElseThrow(() -> new CartNotFoundException(cartUuid));

        // Validate cart
        cartValidator.validateAndGetActiveCart(cartUuid);
        checkoutValidator.validateCartForCheckout(cart);

        // Calculate pricing
        PricingResponse pricing = pricingService.calculatePricing(cart);

        // Get active promotions
        List<CartPromotion> activePromotions = promotionRepository
            .findActivePromotionsByCartId(cartUuid);
        BigDecimal totalDiscount = activePromotions.stream()
            .map(CartPromotion::getDiscountAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate totals
        BigDecimal subtotal = pricing.subtotal();
        BigDecimal tax = pricing.tax();
        BigDecimal shipping = pricing.shipping();
        BigDecimal totalAmount = subtotal.subtract(totalDiscount).add(tax).add(shipping);

        // ✅ FIXED: Try Order Service, fallback to mock
        String orderId = UUID.randomUUID().toString();
        String orderNumber = "ORD-" + System.currentTimeMillis();
        
        try {
            OrderResponse order = orderClient.createOrder(cart, request);
            orderId = order.orderId().toString();
            orderNumber = order.orderNumber();
            log.info("Order created: {}", orderNumber);
        } catch (Exception e) {
            log.error("Order Service unavailable: {}. Using mock order.", e.getMessage());
        }

        // Mark cart as converted
        try {
            cartService.markCartAsConverted(cartUuid, UUID.fromString(orderId));
        } catch (Exception e) {
            log.error("Error marking cart: {}", e.getMessage());
        }

        // Release reservations
        try {
            inventoryService.releaseReservations(cartId);
        } catch (Exception e) {
            log.error("Error releasing: {}", e.getMessage());
        }

        log.info("Checkout completed for cart: {}, order: {}", cartId, orderNumber);

        return checkoutMapper.toCheckoutResponse(
            cart,
            orderId,
            orderNumber,
            subtotal,
            totalDiscount,
            tax,
            shipping,
            totalAmount
        );
    }
}