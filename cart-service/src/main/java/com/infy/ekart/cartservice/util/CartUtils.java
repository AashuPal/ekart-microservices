package com.infy.ekart.cartservice.util;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@UtilityClass
public class CartUtils {

    public static String generateCartKey(UUID cartId) {
        return "cart:" + cartId.toString();
    }

    public static String generateSessionKey(UUID sessionId) {
        return "session:" + sessionId.toString();
    }

    public static BigDecimal roundToTwoDecimals(BigDecimal value) {
        if (value == null) return BigDecimal.ZERO;
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static UUID toUUID(String id) {
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID: " + id);
        }
    }
}