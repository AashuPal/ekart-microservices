package com.infy.ekart.cartservice.service;

import com.infy.ekart.cartservice.dto.response.PromotionResponse;
import com.infy.ekart.cartservice.entity.CartPromotion;

import java.util.List;

public interface CartPromotionService {
    
    PromotionResponse applyPromotion(String cartId, String couponCode);
    
    void removePromotion(String cartId, String promotionId);
    
    List<PromotionResponse> getActivePromotions(String cartId);
}