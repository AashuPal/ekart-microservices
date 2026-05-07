package com.infy.ekart.cartservice.cache;

import com.infy.ekart.cartservice.dto.response.CartResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class CartCacheManager {

    private final RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
    private static final String CART_CACHE_PREFIX = "cart:";
    private static final long CACHE_TTL_HOURS = 24;
    private static final Logger log = LoggerFactory.getLogger(CartCacheManager.class);

    public void cacheCart(String cartId, CartResponse cart) {
        try {
            String key = CART_CACHE_PREFIX + cartId;
            redisTemplate.opsForValue().set(key, cart, CACHE_TTL_HOURS, TimeUnit.HOURS);
            log.debug("Cached cart: {}", cartId);
        } catch (Exception e) {
            log.error("Error caching cart {}: {}", cartId, e.getMessage());
        }
    }

    public Optional<CartResponse> getCachedCart(String cartId) {
        try {
            String key = CART_CACHE_PREFIX + cartId;
            CartResponse cart = (CartResponse) redisTemplate.opsForValue().get(key);
            if (cart != null) {
                log.debug("Cache hit for cart: {}", cartId);
                return Optional.of(cart);
            }
            log.debug("Cache miss for cart: {}", cartId);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error getting cached cart {}: {}", cartId, e.getMessage());
            return Optional.empty();
        }
    }

    public void evictCart(String cartId) {
        try {
            String key = CART_CACHE_PREFIX + cartId;
            redisTemplate.delete(key);
            log.debug("Evicted cart from cache: {}", cartId);
        } catch (Exception e) {
            log.error("Error evicting cart {}: {}", cartId, e.getMessage());
        }
    }
}