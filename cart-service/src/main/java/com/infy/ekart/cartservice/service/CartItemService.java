package com.infy.ekart.cartservice.service;

import com.infy.ekart.cartservice.dto.request.AddItemRequest;
import com.infy.ekart.cartservice.dto.request.UpdateItemQuantityRequest;
import com.infy.ekart.cartservice.dto.response.CartItemResponse;

import java.util.List;

public interface CartItemService {
    
    CartItemResponse addItem(String cartId, AddItemRequest request);
    
    CartItemResponse updateQuantity(String cartId, String itemId, UpdateItemQuantityRequest request);
    
    void removeItem(String cartId, String itemId);
    
    List<CartItemResponse> getCartItems(String cartId);
    
    void clearCart(String cartId);
}