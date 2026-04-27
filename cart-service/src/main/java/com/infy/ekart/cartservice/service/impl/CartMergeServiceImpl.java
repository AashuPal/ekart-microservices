package com.infy.ekart.cartservice.service.impl;

import com.infy.ekart.cartservice.dto.request.MergeCartRequest;
import com.infy.ekart.cartservice.dto.response.CartResponse;
import com.infy.ekart.cartservice.entity.Cart;
import com.infy.ekart.cartservice.entity.CartItem;
import com.infy.ekart.cartservice.enums.CartStatus;
import com.infy.ekart.cartservice.exception.CartNotFoundException;
import com.infy.ekart.cartservice.mapper.CartMapper;
import com.infy.ekart.cartservice.repository.CartItemRepository;
import com.infy.ekart.cartservice.repository.CartRepository;
import com.infy.ekart.cartservice.service.CartMergeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

@Service
@Transactional
public class CartMergeServiceImpl implements CartMergeService {

    private static final Logger log = LoggerFactory.getLogger(CartMergeServiceImpl.class);
    
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CartMapper cartMapper;

    // Manual constructor - properly initialize fields
    public CartMergeServiceImpl(CartRepository cartRepository,
                                 CartItemRepository cartItemRepository,
                                 CartMapper cartMapper) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.cartMapper = cartMapper;
    }

    @Override
    public CartResponse mergeCarts(MergeCartRequest request) {
        log.info("Merging guest cart {} into user cart {}", 
            request.guestCartId(), request.userCartId());

        Cart guestCart = cartRepository.findByIdWithItems(request.guestCartId())
            .orElseThrow(() -> new CartNotFoundException(request.guestCartId()));
        
        Cart userCart = cartRepository.findByIdWithItems(request.userCartId())
            .orElseThrow(() -> new CartNotFoundException(request.userCartId()));

        // Merge items
        for (CartItem guestItem : new ArrayList<>(guestCart.getItems())) {
            Optional<CartItem> existingItem = userCart.getItems().stream()
                .filter(item -> item.getSkuId().equals(guestItem.getSkuId()))
                .findFirst();

            if (existingItem.isPresent()) {
                // Update quantity
                CartItem item = existingItem.get();
                item.setQuantity(item.getQuantity() + guestItem.getQuantity());
                item.calculateTotalPrice();
                cartItemRepository.save(item);
            } else {
                // Move item to user cart
                guestItem.setCart(userCart);
                userCart.addItem(guestItem);
                cartItemRepository.save(guestItem);
            }
        }

        // Clear guest cart
        guestCart.getItems().clear();
        guestCart.setStatus(CartStatus.CONVERTED);
        cartRepository.save(guestCart);

        // Save updated user cart
        userCart = cartRepository.save(userCart);

        log.info("Carts merged successfully");
        return cartMapper.toCartResponse(userCart);
    }
}