package com.infy.ekart.cartservice.service;

import com.infy.ekart.cartservice.dto.response.PricingResponse;
import com.infy.ekart.cartservice.entity.Cart;

public interface CartPricingService {
    
    PricingResponse calculatePricing(Cart cart);
    
    PricingResponse calculatePricing(String cartId);
}