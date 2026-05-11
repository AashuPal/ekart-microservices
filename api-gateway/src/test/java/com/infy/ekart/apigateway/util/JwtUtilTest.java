package com.infy.ekart.apigateway.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private final String secret = "my-secret-key-for-testing-must-be-256-bits-long!!";
    private JwtUtil jwtUtil;
    private SecretKey secretKey;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(secret);               // only one argument
        secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void shouldReturnTrueForValidToken() {
        String token = createToken("user", 3600000);
        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    void shouldReturnFalseForExpiredToken() {
        String token = createToken("user", -1000);
        assertFalse(jwtUtil.validateToken(token));
    }

    @Test
    void shouldReturnFalseForTamperedToken() {
        String token = createToken("user", 3600000);
        String tampered = token.substring(0, token.length() - 5) + "XXXXX";
        assertFalse(jwtUtil.validateToken(tampered));
    }

    @Test
    void shouldReturnFalseForNullToken() {
        assertFalse(jwtUtil.validateToken(null));
    }

    @Test
    void shouldExtractUsername() {
        String token = createToken("john", 3600000);
        assertEquals("john", jwtUtil.getUsernameFromToken(token));
    }

    private String createToken(String subject, long validityMs) {
        Date now = new Date();
        return Jwts.builder()
                .subject(subject)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + validityMs))
                .signWith(secretKey)
                .compact();
    }
}