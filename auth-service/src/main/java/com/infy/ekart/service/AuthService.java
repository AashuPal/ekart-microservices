package com.infy.ekart.service;

import com.infy.ekart.dto.*;
import com.infy.ekart.entity.*;
import com.infy.ekart.repository.CustomerRepository;
import com.infy.ekart.repository.VerificationTokenRepository;
import com.infy.ekart.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final CustomerRepository repo;
    private final VerificationTokenRepository tokenRepo;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;
    private final Map<String, String> otpStore = new HashMap<>();

    public AuthService(CustomerRepository repo,
                       VerificationTokenRepository tokenRepo,
                       PasswordEncoder encoder,
                       JwtUtil jwtUtil,
                       EmailService emailService) {
        this.repo = repo;
        this.tokenRepo = tokenRepo;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
    }

    public ApiResponse register(CustomerDTO dto) {
        if (repo.findByEmailId(dto.getEmailId()).isPresent()) {
            throw new RuntimeException("User already exists");
        }

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

        log.info("User registered: {}", dto.getEmailId());
        return new ApiResponse("Registered successfully. Please verify your email.", true);
    }

    public LoginResponse login(LoginRequest req) {
        Customer user = repo.findByEmailId(req.email())
            .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!encoder.matches(req.password(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getEmailId(), user.getName());

        log.info("User logged in: {}", req.email());

        // ✅ FIXED: Return proper LoginResponse with actual values
        return new LoginResponse(
            token,                  // token
            user.getEmailId(),      // email
            user.getName(),         // name
            user.getPhoneNumber(),  // phoneNumber
            3600                    // expiresIn (1 hour)
        );
    }

    public String verifyEmail(String token) {
        VerificationToken vt = tokenRepo.findByToken(token)
            .orElseThrow(() -> new RuntimeException("Invalid verification token"));

        if (vt.getExpiryDate().before(new Date())) {
            throw new RuntimeException("Verification token has expired");
        }

        Customer user = repo.findByEmailId(vt.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));

        user.setVerified(true);
        repo.save(user);
        tokenRepo.delete(vt);

        log.info("Email verified for: {}", vt.getEmail());
        return "Email verified successfully";
    }

    public String sendOtp(String phone) {
        String otp = String.format("%06d", new Random().nextInt(900000) + 100000);
        otpStore.put(phone, otp);
        log.info("OTP sent to {}: {}", phone, otp);
        return "OTP sent successfully: " + otp;
    }

    public boolean verifyOtp(String phone, String otp) {
        String storedOtp = otpStore.get(phone);
        if (storedOtp != null && storedOtp.equals(otp)) {
            otpStore.remove(phone);
            return true;
        }
        return false;
    }
}