package com.infy.ekart.notificationservice.controller;

import com.infy.ekart.notificationservice.service.FirebaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class NotificationController {

    private final FirebaseService firebaseService;

    public NotificationController(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }

    // 1️⃣ Welcome + Verification
    @PostMapping("/send-welcome")
    public ResponseEntity<?> sendWelcome(@RequestBody Map<String, String> req) {
        String email = req.get("email");
        String name = req.getOrDefault("name", "Customer");
        String type = req.getOrDefault("type", "link");

        Map<String, Object> result = "code".equals(type) 
            ? firebaseService.sendVerificationCode(email, name)
            : firebaseService.sendVerificationEmail(email, name);
        
        return ResponseEntity.ok(result);
    }

    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody Map<String, String> req) {
        boolean ok = firebaseService.verifyCode(req.get("email"), req.get("code"));
        return ok ? ResponseEntity.ok(Map.of("success", true))
                  : ResponseEntity.badRequest().body(Map.of("error", "Invalid code"));
    }

    // 2️⃣ Order Confirmation
    @PostMapping("/order-confirmation")
    public ResponseEntity<?> orderConfirm(@RequestBody Map<String, String> req) {
        return ResponseEntity.ok(firebaseService.sendOrderConfirmation(req));
    }

    // 3️⃣ Google Sign-In
    @PostMapping("/verify-google")
    public ResponseEntity<?> verifyGoogle(@RequestBody Map<String, String> req) {
        Map<String, Object> user = firebaseService.verifyGoogleToken(req.get("idToken"));
        return user != null ? ResponseEntity.ok(Map.of("success", true, "user", user))
                            : ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
    }

    // 4️⃣ Password Reset
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> req) {
        return ResponseEntity.ok(firebaseService.sendPasswordReset(req.get("email")));
    }

    // 5️⃣ Shipping Update
    @PostMapping("/shipping-update")
    public ResponseEntity<?> shippingUpdate(@RequestBody Map<String, String> req) {
        return ResponseEntity.ok(firebaseService.sendShippingUpdate(req));
    }

    // 6️⃣ Payment Confirmation
    @PostMapping("/payment-confirmation")
    public ResponseEntity<?> paymentConfirm(@RequestBody Map<String, String> req) {
        return ResponseEntity.ok(firebaseService.sendPaymentConfirmation(req));
    }
}