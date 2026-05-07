package com.infy.ekart.cartservice.service;

import com.infy.ekart.cartservice.dto.request.CheckoutRequest;
import com.infy.ekart.cartservice.dto.response.CheckoutResponse;

public interface CartCheckoutService {
    
    CheckoutResponse checkout(String cartId, CheckoutRequest request);
}