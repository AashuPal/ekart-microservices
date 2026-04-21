package com.infy.ekart.controller;

import com.infy.ekart.dto.*;
import com.infy.ekart.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService service;

    @PostMapping("/register")
    public ApiResponse register(@Valid @RequestBody CustomerDTO dto) {
        return service.register(dto);
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest req) {
        return service.login(req);
    }

    @GetMapping("/verify")
    public String verify(@RequestParam String token) {
        return service.verifyEmail(token);
    }

    @PostMapping("/otp")
    public String sendOtp(@RequestParam String phone) {
        return service.sendOtp(phone);
    }

    @PostMapping("/otp/verify")
    public boolean verifyOtp(@RequestParam String phone, @RequestParam String otp) {
        return service.verifyOtp(phone, otp);
    }
}