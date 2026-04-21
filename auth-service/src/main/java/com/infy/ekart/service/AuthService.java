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
    private CustomerRepository repo;

    @Autowired
    private VerificationTokenRepository tokenRepo;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService;

    private Map<String, String> otpStore = new HashMap<>();

    public ApiResponse register(CustomerDTO dto) {

        if (repo.existsByEmailId(dto.getEmailId()))
            throw new RuntimeException("User exists");

        Customer user = new Customer();
        user.setEmailId(dto.getEmailId());
        user.setName(dto.getName());
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setAddress(dto.getAddress());
        user.setVerified(false);

        repo.save(user);

        String token = UUID.randomUUID().toString();

        VerificationToken vt = new VerificationToken();
        vt.setEmail(dto.getEmailId());
        vt.setToken(token);
        vt.setExpiryDate(new Date(System.currentTimeMillis() + 600000));

        tokenRepo.save(vt);

        emailService.sendEmail(dto.getEmailId(), token);

        return new ApiResponse("Registered. Verify email.", true);
    }

    public String login(LoginRequest req) {

        Customer user = repo.findById(req.getEmail())
                .orElseThrow(() -> new RuntimeException("Not found"));

        if (!encoder.matches(req.getPassword(), user.getPassword()))
            throw new RuntimeException("Wrong password");

        if (!user.isVerified())
            throw new RuntimeException("Verify email first");

        return jwtUtil.generateToken(user.getEmailId());
    }

    public String verifyEmail(String token) {

        VerificationToken vt = tokenRepo.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (vt.getExpiryDate().before(new Date()))
            throw new RuntimeException("Expired");

        Customer user = repo.findById(vt.getEmail()).get();
        user.setVerified(true);
        repo.save(user);

        tokenRepo.delete(vt);

        return "Verified";
    }

    public String sendOtp(String phone) {
        String otp = String.valueOf(new Random().nextInt(900000) + 100000);
        otpStore.put(phone, otp);
        return "OTP: " + otp;
    }

    public boolean verifyOtp(String phone, String otp) {
        return otp.equals(otpStore.get(phone));
    }
}