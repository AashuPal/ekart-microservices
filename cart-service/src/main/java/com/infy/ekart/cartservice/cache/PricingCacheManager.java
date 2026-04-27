package com.infy.ekart.cartservice.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class PricingCacheManager {

    private final RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
    private static final String PRICE_CACHE_PREFIX = "price:";
    private static final long CACHE_TTL_MINUTES = 30;
    private static final Logger log = LoggerFactory.getLogger(PricingCacheManager.class);

    public void cachePrice(UUID skuId, BigDecimal price) {
        try {
            String key = PRICE_CACHE_PREFIX + skuId.toString();
            redisTemplate.opsForValue().set(key, price, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
            log.debug("Cached price for SKU {}: {}", skuId, price);
        } catch (Exception e) {
            log.error("Error caching price for SKU {}: {}", skuId, e.getMessage());
        }
    }

    public Optional<BigDecimal> getCachedPrice(UUID skuId) {
        try {
            String key = PRICE_CACHE_PREFIX + skuId.toString();
            Object price = redisTemplate.opsForValue().get(key);
            if (price instanceof BigDecimal) {
                log.debug("Price cache hit for SKU: {}", skuId);
                return Optional.of((BigDecimal) price);
            }
            log.debug("Price cache miss for SKU: {}", skuId);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error getting cached price for SKU {}: {}", skuId, e.getMessage());
            return Optional.empty();
        }
    }
}