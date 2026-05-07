package com.infy.ekart.cartservice.service;

import com.infy.ekart.cartservice.dto.request.MergeCartRequest;
import com.infy.ekart.cartservice.dto.response.CartResponse;

public interface CartMergeService {
    
    CartResponse mergeCarts(MergeCartRequest request);
}