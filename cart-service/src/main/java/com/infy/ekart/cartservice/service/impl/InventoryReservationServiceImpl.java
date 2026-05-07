package com.infy.ekart.cartservice.service.impl;

import com.infy.ekart.cartservice.entity.CartItem;
import com.infy.ekart.cartservice.entity.ProductReservation;
import com.infy.ekart.cartservice.enums.ReservationStatus;
import com.infy.ekart.cartservice.exception.ItemNotFoundException;
import com.infy.ekart.cartservice.repository.CartItemRepository;
import com.infy.ekart.cartservice.repository.ProductReservationRepository;
import com.infy.ekart.cartservice.service.InventoryReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class InventoryReservationServiceImpl implements InventoryReservationService {

    private static final Logger log = LoggerFactory.getLogger(InventoryReservationServiceImpl.class);

    private final ProductReservationRepository reservationRepository;
    private final CartItemRepository cartItemRepository;

    // Manual constructor
    public InventoryReservationServiceImpl(ProductReservationRepository reservationRepository,
                                            CartItemRepository cartItemRepository) {
        this.reservationRepository = reservationRepository;
        this.cartItemRepository = cartItemRepository;
    }

    @Override
    public ProductReservation reserveInventory(UUID cartItemId, UUID productId, int quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new ItemNotFoundException(cartItemId));

        // Create ProductReservation without builder
        ProductReservation reservation = new ProductReservation();
        reservation.setCartItem(cartItem);
        reservation.setProductId(productId);
        reservation.setQuantityReserved(quantity);
        reservation.setReservationExpiry(LocalDateTime.now().plusMinutes(15));
        reservation.setStatus(ReservationStatus.RESERVED);
        reservation.setCreatedAt(LocalDateTime.now());

        reservation = reservationRepository.save(reservation);
        log.info("Reserved {} units of product {} for cart item {}", quantity, productId, cartItemId);
        
        return reservation;
    }

    @Override
    public void releaseReservation(UUID reservationId) {
        reservationRepository.findById(reservationId).ifPresent(reservation -> {
            reservation.setStatus(ReservationStatus.RELEASED);
            reservationRepository.save(reservation);
            log.info("Released reservation: {}", reservationId);
        });
    }

    @Override
    public void releaseReservations(String cartId) {
        UUID cartUuid = UUID.fromString(cartId);
        reservationRepository.updateStatusByCartId(cartUuid, ReservationStatus.RELEASED);
        log.info("Released all reservations for cart: {}", cartId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductReservation> getReservationsByCartId(UUID cartId) {
        return reservationRepository.findByCartId(cartId);
    }

    @Override
    public void extendReservation(UUID reservationId, int additionalMinutes) {
        reservationRepository.findById(reservationId).ifPresent(reservation -> {
            LocalDateTime newExpiry = LocalDateTime.now().plusMinutes(additionalMinutes);
            reservation.setReservationExpiry(newExpiry);
            reservation.setStatus(ReservationStatus.RESERVED);
            reservationRepository.save(reservation);
            log.info("Extended reservation {} for {} minutes", reservationId, additionalMinutes);
        });
    }

    @Override
    public void expireReservations() {
        LocalDateTime now = LocalDateTime.now();
        var expiredReservations = reservationRepository
            .findByStatusAndReservationExpiryBefore(ReservationStatus.RESERVED, now);
        
        expiredReservations.forEach(reservation -> {
            reservation.setStatus(ReservationStatus.EXPIRED);
            reservationRepository.save(reservation);
        });
        
        log.info("Expired {} reservations", expiredReservations.size());
    }
}