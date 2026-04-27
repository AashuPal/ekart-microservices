package com.infy.ekart.controller;

import com.infy.ekart.dto.*;
import com.infy.ekart.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody CustomerDTO dto) {
        ApiResponse response = service.register(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        LoginResponse response = service.login(req);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verify(@RequestParam String token) {
        String result = service.verifyEmail(token);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/otp")
    public ResponseEntity<String> sendOtp(@RequestParam String phone) {
        String result = service.sendOtp(phone);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/otp/verify")
    public ResponseEntity<Boolean> verifyOtp(@RequestParam String phone, @RequestParam String otp) {
        boolean result = service.verifyOtp(phone, otp);
        return ResponseEntity.ok(result);
    }
}