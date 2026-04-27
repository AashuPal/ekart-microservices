package com.infy.ekart.cartservice.service;

import com.infy.ekart.cartservice.dto.request.CreateCartRequest;
import com.infy.ekart.cartservice.dto.request.MergeCartRequest;
import com.infy.ekart.cartservice.dto.response.CartResponse;
import com.infy.ekart.cartservice.entity.Cart;

import java.util.UUID;

public interface CartService {
    
    CartResponse createCart(CreateCartRequest request);
    
    CartResponse getCartById(String cartId);
    
    Cart getCartEntity(UUID cartId);
    
    CartResponse updateCartStatus(String cartId, String status);
    
    void deleteCart(String cartId);
    
    CartResponse mergeCarts(MergeCartRequest request);
    
    void markCartAsConverted(UUID cartId, UUID orderId);
    
    void expireInactiveCarts();
}