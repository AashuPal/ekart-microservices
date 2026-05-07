package com.infy.ekart.cartservice.service;

import com.infy.ekart.cartservice.dto.response.CartResponse;

public interface AbandonedCartService {
    
    void markCartAsAbandoned(String cartId);
    
    void sendAbandonedCartReminders();
    
    CartResponse recoverCart(String cartId);
}