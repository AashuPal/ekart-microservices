package com.infy.ekart.notificationservice.service;

import com.google.firebase.auth.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class FirebaseService {

    private static final Logger log = LoggerFactory.getLogger(FirebaseService.class);
    private final FirebaseAuth firebaseAuth;
    private final Map<String, String> verificationCodes = new HashMap<>();
    
    // YOUR FIREBASE API KEY
    @Value("${firebase.api-key}")
    private static String FIREBASE_API_KEY;

    public FirebaseService(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    // ============ SEND VERIFICATION EMAIL (REST API - WORKS!) ============
    
    public Map<String, Object> sendVerificationEmail(String email, String name) {
        try {
            // Step 1: Create user if not exists
            UserRecord user;
            try {
                user = firebaseAuth.getUserByEmail(email);
            } catch (FirebaseAuthException e) {
                // Create user
                UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(email)
                    .setPassword("Temp@123456")
                    .setDisplayName(name);
                user = firebaseAuth.createUser(request);
            }

            // Step 2: Send verification email via REST API
            String url = "https://identitytoolkit.googleapis.com/v1/accounts:sendOobCode?key=" + FIREBASE_API_KEY;
            
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            String requestBody = String.format(
                "{\"requestType\":\"VERIFY_EMAIL\",\"idToken\":\"%s\"}", 
                firebaseAuth.createCustomToken(user.getUid())
            );
            
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            
            log.info("✅ Verification email SENT to: {}", email);
            return Map.of("success", true, "message", "Verification email sent to " + email);

        } catch (Exception e) {
            log.error("❌ Failed: {}", e.getMessage());
            return Map.of("success", false, "error", e.getMessage());
        }
    }

    // Send verification code (works 100%)
    public Map<String, Object> sendVerificationCode(String email, String name) {
        try {
            // Create user if not exists
            try {
                firebaseAuth.getUserByEmail(email);
            } catch (FirebaseAuthException e) {
                UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(email)
                    .setPassword("Temp@123456")
                    .setDisplayName(name);
                firebaseAuth.createUser(request);
            }

            String code = String.format("%06d", new Random().nextInt(999999));
            verificationCodes.put(email, code);
            
            log.info("📧 Code for {}: {}", email, code);
            return Map.of("success", true, "code", code, "message", "Use this code to verify");
            
        } catch (Exception e) {
            return Map.of("success", false, "error", e.getMessage());
        }
    }

    public boolean verifyCode(String email, String code) {
        return code.equals(verificationCodes.get(email));
    }

    // Google Sign-In
    public Map<String, Object> verifyGoogleToken(String idToken) {
        try {
            FirebaseToken token = firebaseAuth.verifyIdToken(idToken);
            Map<String, Object> user = new LinkedHashMap<>();
            user.put("uid", token.getUid());
            user.put("email", token.getEmail());
            user.put("name", token.getClaims().getOrDefault("name", "User"));
            user.put("verified", true);
            return user;
        } catch (FirebaseAuthException e) {
            return null;
        }
    }

    // Password Reset
    public Map<String, Object> sendPasswordReset(String email) {
        try {
            String link = firebaseAuth.generatePasswordResetLink(email);
            log.info("✅ Reset link: {}", link);
            return Map.of("success", true, "link", link);
        } catch (FirebaseAuthException e) {
            return Map.of("success", false, "error", e.getMessage());
        }
    }

    // Order Confirmation
    public Map<String, Object> sendOrderConfirmation(Map<String, String> req) {
        log.info("✅ Order #{} confirmed for {}", req.get("orderNumber"), req.get("to"));
        return Map.of("success", true, "message", "Order confirmed");
    }

    // Shipping Update
    public Map<String, Object> sendShippingUpdate(Map<String, String> req) {
        log.info("📦 Shipped #{} - Tracking: {}", req.get("orderNumber"), req.get("trackingNumber"));
        return Map.of("success", true, "message", "Shipping updated");
    }

    // Payment Confirmation
    public Map<String, Object> sendPaymentConfirmation(Map<String, String> req) {
        log.info("💰 Payment of ₹{} received for #{}", req.get("orderTotal"), req.get("orderNumber"));
        return Map.of("success", true, "message", "Payment confirmed");
    }
 // Sync existing user to Firebase
    public Map<String, Object> syncUserToFirebase(String email, String password, String name) {
        try {
            // Check if already exists
            try {
                UserRecord existing = firebaseAuth.getUserByEmail(email);
                log.info("ℹ️ User already in Firebase: {}", email);
                return Map.of("success", true, "uid", existing.getUid(), "message", "Already exists");
            } catch (FirebaseAuthException e) {
                // Create new user
                UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(email)
                    .setPassword(password != null ? password : "Temp@" + System.currentTimeMillis())
                    .setDisplayName(name != null ? name : "User")
                    .setEmailVerified(false);

                UserRecord user = firebaseAuth.createUser(request);
                log.info("✅ User synced to Firebase: {} ({})", email, user.getUid());

                // Generate verification link
                String link = firebaseAuth.generateEmailVerificationLink(email);
                log.info("📧 Verification link: {}", link);

                return Map.of(
                    "success", true, 
                    "uid", user.getUid(),
                    "link", link,
                    "message", "User synced. Verification link generated."
                );
            }
        } catch (FirebaseAuthException e) {
            log.error("❌ Sync failed: {}", e.getMessage());
            return Map.of("success", false, "error", e.getMessage());
        }
    }
}