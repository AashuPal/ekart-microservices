package com.infy.ekart.cartservice.service.impl;

import com.infy.ekart.cartservice.dto.request.CreateCartRequest;
import com.infy.ekart.cartservice.dto.request.MergeCartRequest;
import com.infy.ekart.cartservice.dto.response.CartResponse;
import com.infy.ekart.cartservice.entity.Cart;
import com.infy.ekart.cartservice.entity.CartItem;
import com.infy.ekart.cartservice.enums.CartStatus;
import com.infy.ekart.cartservice.exception.CartNotFoundException;
import com.infy.ekart.cartservice.mapper.CartMapper;
import com.infy.ekart.cartservice.repository.CartRepository;
import com.infy.ekart.cartservice.service.CartService;
import com.infy.ekart.cartservice.validator.CartValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    private static final Logger log = LoggerFactory.getLogger(CartServiceImpl.class);

    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final CartValidator cartValidator;

    public CartServiceImpl(CartRepository cartRepository,
                           CartMapper cartMapper,
                           CartValidator cartValidator) {
        this.cartRepository = cartRepository;
        this.cartMapper = cartMapper;
        this.cartValidator = cartValidator;
    }

    @Override
    public CartResponse createCart(CreateCartRequest request) {
        log.info("Creating new cart for user: {}", request.userId());
        
        Cart cart = new Cart();
        cart.setUserId(request.userId());
        cart.setSessionId(request.sessionId());
        cart.setStatus(CartStatus.ACTIVE);
        cart.setExpiresAt(LocalDateTime.now().plusHours(24));

        cart = cartRepository.save(cart);
        log.info("Cart created successfully: {}", cart.getId());
        
        return cartMapper.toCartResponse(cart);
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCartById(String cartId) {
        UUID id = UUID.fromString(cartId);
        Cart cart = cartValidator.validateAndGetActiveCart(id);
        return cartMapper.toCartResponse(cart);
    }

    @Override
    @Transactional(readOnly = true)
    public Cart getCartEntity(UUID cartId) {
        return cartRepository.findByIdWithItemsAndPromotions(cartId)
            .orElseThrow(() -> new CartNotFoundException(cartId));
    }

    @Override
    public CartResponse updateCartStatus(String cartId, String status) {
        UUID id = UUID.fromString(cartId);
        Cart cart = cartRepository.findById(id)
            .orElseThrow(() -> new CartNotFoundException(id));
        
        cart.setStatus(CartStatus.valueOf(status.toUpperCase()));
        cart = cartRepository.save(cart);
        
        log.info("Cart {} status updated to {}", cartId, status);
        return cartMapper.toCartResponse(cart);
    }

    @Override
    public void deleteCart(String cartId) {
        UUID id = UUID.fromString(cartId);
        if (!cartRepository.existsById(id)) {
            throw new CartNotFoundException(id);
        }
        cartRepository.deleteById(id);
        log.info("Cart deleted: {}", cartId);
    }

    @Override
    public CartResponse mergeCarts(MergeCartRequest request) {
        log.info("Merging carts - Guest: {}, User: {}", request.guestCartId(), request.userCartId());
        
        Cart guestCart = cartRepository.findByIdWithItems(request.guestCartId())
            .orElseThrow(() -> new CartNotFoundException(request.guestCartId()));
        
        Cart userCart = cartRepository.findByIdWithItems(request.userCartId())
            .orElseThrow(() -> new CartNotFoundException(request.userCartId()));

        // Create a copy to avoid ConcurrentModificationException
        List<CartItem> guestItems = new ArrayList<>(guestCart.getItems());
        
        for (CartItem item : guestItems) {
            item.setCart(userCart);
            userCart.getItems().add(item);
        }

        guestCart.getItems().clear();
        guestCart.setStatus(CartStatus.CONVERTED);
        cartRepository.save(guestCart);
        
        userCart = cartRepository.save(userCart);
        
        log.info("Carts merged successfully");
        return cartMapper.toCartResponse(userCart);
    }

    @Override
    public void markCartAsConverted(UUID cartId, UUID orderId) {
        Cart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new CartNotFoundException(cartId));
        
        cart.setStatus(CartStatus.CONVERTED);
        cartRepository.save(cart);
        
        log.info("Cart {} marked as CONVERTED for order {}", cartId, orderId);
    }

    @Override
    public void expireInactiveCarts() {
        LocalDateTime now = LocalDateTime.now();
        List<Cart> expiredCarts = cartRepository.findByStatusAndExpiresAtBefore(CartStatus.ACTIVE, now);
        
        for (Cart cart : expiredCarts) {
            cart.setStatus(CartStatus.EXPIRED);
            cartRepository.save(cart);
        }
        
        log.info("Expired {} inactive carts", expiredCarts.size());
    }
}