package com.infy.ekart.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final String secret = "mysupersecurekeymysupersecurekey12345678";
    private final long expirationMs = 3600000;

    private Key getSigningKey() {
        return new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
                SignatureAlgorithm.HS256.getJcaName());
    }

    public String generateToken(String email, String role, String name) {
        return Jwts.builder()
            .setSubject(email)
            .claim("role", role)
            .claim("name", name)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    public Claims validateToken(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    public String getEmailFromToken(String token) {
        return validateToken(token).getSubject();
    }
}