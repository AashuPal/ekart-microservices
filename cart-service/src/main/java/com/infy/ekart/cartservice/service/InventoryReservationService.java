package com.infy.ekart.cartservice.service;

import com.infy.ekart.cartservice.entity.ProductReservation;

import java.util.List;
import java.util.UUID;

public interface InventoryReservationService {
    
    ProductReservation reserveInventory(UUID cartItemId, UUID productId, int quantity);
    
    void releaseReservation(UUID reservationId);
    
    void releaseReservations(String cartId);
    
    List<ProductReservation> getReservationsByCartId(UUID cartId);
    
    void extendReservation(UUID reservationId, int additionalMinutes);
    
    void expireReservations();
}