package com.infy.ekart.service;

import com.infy.ekart.dto.*;
import com.infy.ekart.entity.*;
import com.infy.ekart.repository.*;
import com.infy.ekart.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AuthService {

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private VerificationTokenRepository tokenRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private Map<String, String> otpStore = new HashMap<>();

    public ApiResponse register(CustomerDTO dto) {

        if (customerRepo.existsById(dto.getEmailId())) {
            throw new RuntimeException("User already exists");
        }

        Customer user = new Customer();
        user.setEmailId(dto.getEmailId());
        user.setName(dto.getName());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setAddress(dto.getAddress());
        user.setVerified(false);

        customerRepo.save(user);

        // Email token
        String token = UUID.randomUUID().toString();

        VerificationToken vt = new VerificationToken();
        vt.setEmail(dto.getEmailId());
        vt.setToken(token);
        vt.setExpiryDate(new Date(System.currentTimeMillis() + 600000));

        tokenRepo.save(vt);

        System.out.println("Verify: http://localhost:8081/auth/verify?token=" + token);

        return new ApiResponse("Registered. Verify email.", true);
    }

    public String login(LoginRequest req) {

        Customer user = customerRepo.findById(req.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        if (!user.isVerified()) {
            throw new RuntimeException("Email not verified");
        }

        return jwtUtil.generateToken(user.getEmailId());
    }

    public String verifyEmail(String token) {
        VerificationToken vt = tokenRepo.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        Customer user = customerRepo.findById(vt.getEmail()).get();
        user.setVerified(true);

        customerRepo.save(user);

        return "Email verified";
    }

    // OTP
    public String sendOtp(String phone) {
        String otp = String.valueOf(new Random().nextInt(900000) + 100000);
        otpStore.put(phone, otp);
        return "OTP: " + otp;
    }

    public boolean verifyOtp(String phone, String otp) {
        return otp.equals(otpStore.get(phone));
    }
}