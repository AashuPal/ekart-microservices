package com.infy.ekart.controller;

import com.infy.ekart.dto.*;
import com.infy.ekart.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    // ---------- REGISTER ----------
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody CustomerDTO dto) {
        ApiResponse response = service.register(dto);
        return ResponseEntity.ok(response);
    }

    // ---------- LOGIN ----------
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        LoginResponse response = service.login(req);
        return ResponseEntity.ok(response);
    }

    // ---------- VERIFY EMAIL (from link) ----------
    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        String message = service.verifyEmail(token);
        return ResponseEntity.ok(message);
    }

    // ---------- RESEND VERIFICATION ----------
    @PostMapping("/resend-verification")
    public ResponseEntity<ApiResponse> resendVerification(@RequestParam String email) {
        ApiResponse response = service.resendVerification(email);
        return ResponseEntity.ok(response);
    }

    // ---------- GOOGLE OAUTH2 ----------
//    @PostMapping("/google")
//    public ResponseEntity<LoginResponse> googleLogin(@RequestBody Map<String, String> body) {
//        String code = body.get("code");
//        LoginResponse response = service.googleLogin(code);
//        return ResponseEntity.ok(response);
//    }
    @PostMapping("/google")
    public ResponseEntity<LoginResponse> googleLogin(@RequestBody Map<String, String> body) {
        String code = body.get("code");  // Extract ONLY the code from the JSON body
        if (code == null || code.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        LoginResponse response = service.googleLogin(code);
        return ResponseEntity.ok(response);
    }

    // ---------- OTP ----------
    @PostMapping("/otp")
    public ResponseEntity<String> sendOtp(@RequestParam String phone) {
        String result = service.sendOtp(phone);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/otp/verify")
    public ResponseEntity<LoginResponse> verifyOtp(@RequestParam String phone,
                                                   @RequestParam String otp) {
        LoginResponse response = service.verifyOtp(phone, otp);
        return ResponseEntity.ok(response);
    }

    // ---------- PASSWORD ----------
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(@RequestParam String email) {
        ApiResponse response = service.forgotPassword(email);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@RequestParam String token,
                                                     @RequestParam String newPassword) {
        ApiResponse response = service.resetPassword(token, newPassword);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse> changePassword(
            @RequestHeader("X-User-Email") String email,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        ApiResponse response = service.changePassword(email, oldPassword, newPassword);
        return ResponseEntity.ok(response);
    }

    // ---------- USERS (Admin) ----------
    @GetMapping("/users")
    public ResponseEntity<List<LoginResponse>> getAllUsers() {
        return ResponseEntity.ok(service.getAllUsers());
    }

    @GetMapping("/user/{email}")
    public ResponseEntity<LoginResponse> getUserByEmail(@PathVariable String email) {
        // This method is still in AuthService? We removed it, so we need to add it back quickly
        // Let's add a simple version in the service
        LoginResponse response = service.getUserByEmail(email);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/users/{email}/role")
    public ResponseEntity<String> updateUserRole(
            @RequestHeader("X-User-Email") String requesterEmail,
            @RequestHeader("X-User-Role") String requesterRole,
            @PathVariable String email,
            @RequestParam String role) {

        if (!"ROLE_ADMIN".equals(requesterRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only Super Admin can change roles");
        }
        String result = service.updateUserRole(email, role, requesterEmail);
        return ResponseEntity.ok(result);
    }
    
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse> updateProfile(
            @RequestHeader("X-User-Email") String currentEmail,   // still the PK, not the new verified email
            @Valid @RequestBody UpdateProfileRequest request) {
        ApiResponse response = service.updateProfile(currentEmail, request);
        return ResponseEntity.ok(response);
    }
    

    @DeleteMapping("/users/{email}")
    public ResponseEntity<ApiResponse> deleteUser(
            @RequestHeader("X-User-Email") String requesterEmail,
            @RequestHeader("X-User-Role") String requesterRole,
            @PathVariable String email) {

        if (!"ROLE_ADMIN".equals(requesterRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse("Only Super Admin can delete users", false));
        }
        ApiResponse response = service.deleteUser(email, requesterEmail);
        return ResponseEntity.ok(response);
    }
}