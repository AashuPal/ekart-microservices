package com.infy.ekart.cartservice.service.impl;

import com.infy.ekart.cartservice.client.NotificationServiceClient;
import com.infy.ekart.cartservice.dto.response.CartResponse;
import com.infy.ekart.cartservice.entity.AbandonedCart;
import com.infy.ekart.cartservice.entity.Cart;
import com.infy.ekart.cartservice.enums.CartStatus;
import com.infy.ekart.cartservice.exception.CartNotFoundException;
import com.infy.ekart.cartservice.mapper.CartMapper;
import com.infy.ekart.cartservice.repository.AbandonedCartRepository;
import com.infy.ekart.cartservice.repository.CartRepository;
import com.infy.ekart.cartservice.service.AbandonedCartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AbandonedCartServiceImpl implements AbandonedCartService {

    private static final Logger log = LoggerFactory.getLogger(AbandonedCartServiceImpl.class);

    private final CartRepository cartRepository;
    private final AbandonedCartRepository abandonedCartRepository;
    private final NotificationServiceClient notificationClient;
    private final CartMapper cartMapper;

    public AbandonedCartServiceImpl(CartRepository cartRepository,
                                     AbandonedCartRepository abandonedCartRepository,
                                     NotificationServiceClient notificationClient,
                                     CartMapper cartMapper) {
        this.cartRepository = cartRepository;
        this.abandonedCartRepository = abandonedCartRepository;
        this.notificationClient = notificationClient;
        this.cartMapper = cartMapper;
    }

    @Override
    public void markCartAsAbandoned(String cartId) {
        UUID cartUuid = UUID.fromString(cartId);
        Cart cart = cartRepository.findById(cartUuid)
            .orElseThrow(() -> new CartNotFoundException(cartUuid));

        cart.setStatus(CartStatus.ABANDONED);
        cartRepository.save(cart);

        AbandonedCart abandonedCart = new AbandonedCart();
        abandonedCart.setCartId(cartUuid);
        abandonedCart.setUserId(cart.getUserId());
        abandonedCart.setEmailSent(false);
        abandonedCart.setStatus("PENDING");
        abandonedCart.setRecoveryUrl("https://ekart.com/cart/recover/" + cartId);
        abandonedCart.setCreatedAt(LocalDateTime.now());

        abandonedCartRepository.save(abandonedCart);
        log.info("Cart {} marked as abandoned", cartId);
    }

    @Override
    @Scheduled(cron = "0 0 */6 * * *")
    public void sendAbandonedCartReminders() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusHours(24);
        List<AbandonedCart> abandonedCarts = abandonedCartRepository
            .findAbandonedCartsForReminder(cutoffDate);

        for (AbandonedCart abandonedCart : abandonedCarts) {
            if (abandonedCart.getUserId() != null) {
                notificationClient.sendAbandonedCartReminder(
                    abandonedCart.getUserId(),
                    abandonedCart.getCartId(),
                    abandonedCart.getRecoveryUrl()
                );

                abandonedCart.setEmailSent(true);
                abandonedCart.setLastReminderAt(LocalDateTime.now());
                abandonedCartRepository.save(abandonedCart);
            }
        }

        log.info("Sent {} abandoned cart reminders", abandonedCarts.size());
    }

    @Override
    public CartResponse recoverCart(String cartId) {
        UUID cartUuid = UUID.fromString(cartId);
        Cart cart = cartRepository.findById(cartUuid)
            .orElseThrow(() -> new CartNotFoundException(cartUuid));

        cart.setStatus(CartStatus.ACTIVE);
        cart.setExpiresAt(LocalDateTime.now().plusHours(24));
        cart = cartRepository.save(cart);

        abandonedCartRepository.findByCartId(cartUuid).ifPresent(ac -> {
            ac.setStatus("RECOVERED");
            abandonedCartRepository.save(ac);
        });

        log.info("Cart {} recovered", cartId);
        return cartMapper.toCartResponse(cart);
    }
}