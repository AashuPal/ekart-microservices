package com.infy.ekart.cartservice.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@UtilityClass
public class DateTimeUtils {

    private static final ZoneId DEFAULT_ZONE = ZoneId.of("Asia/Kolkata");

    public static LocalDateTime now() {
        return LocalDateTime.now(DEFAULT_ZONE);
    }

    public static LocalDateTime addHours(LocalDateTime dateTime, int hours) {
        return dateTime.plusHours(hours);
    }

    public static LocalDateTime addMinutes(LocalDateTime dateTime, int minutes) {
        return dateTime.plusMinutes(minutes);
    }

    public static LocalDateTime addDays(LocalDateTime dateTime, int days) {
        return dateTime.plusDays(days);
    }

    public static long hoursBetween(LocalDateTime from, LocalDateTime to) {
        return ChronoUnit.HOURS.between(from, to);
    }

    public static long minutesBetween(LocalDateTime from, LocalDateTime to) {
        return ChronoUnit.MINUTES.between(from, to);
    }

    public static boolean isExpired(LocalDateTime expiryTime) {
        return now().isAfter(expiryTime);
    }

    public static LocalDateTime getDefaultExpiryTime() {
        return now().plusHours(24);
    }

    public static LocalDateTime getDefaultReservationExpiry() {
        return now().plusMinutes(15);
    }
}