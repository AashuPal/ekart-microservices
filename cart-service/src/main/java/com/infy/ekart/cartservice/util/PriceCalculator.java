package com.infy.ekart.cartservice.util;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;

@UtilityClass
public class PriceCalculator {

    private static final BigDecimal TAX_RATE = new BigDecimal("0.18"); // 18% GST
    private static final BigDecimal FREE_SHIPPING_THRESHOLD = new BigDecimal("500");
    private static final BigDecimal STANDARD_SHIPPING = new BigDecimal("50");

    public static BigDecimal calculateSubtotal(BigDecimal unitPrice, int quantity) {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public static BigDecimal calculateTax(BigDecimal subtotal) {
        return subtotal.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal calculateShipping(BigDecimal subtotal) {
        if (subtotal.compareTo(FREE_SHIPPING_THRESHOLD) >= 0) {
            return BigDecimal.ZERO;
        }
        return STANDARD_SHIPPING;
    }

    public static BigDecimal calculateDiscount(BigDecimal subtotal, BigDecimal discountPercent) {
        return subtotal.multiply(discountPercent)
            .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }

    public static BigDecimal calculateTotal(BigDecimal subtotal, BigDecimal discount, 
                                             BigDecimal tax, BigDecimal shipping) {
        return subtotal.subtract(discount).add(tax).add(shipping);
    }
}