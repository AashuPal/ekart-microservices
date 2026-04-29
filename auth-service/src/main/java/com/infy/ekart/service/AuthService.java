package com.infy.ekart.service;

import com.infy.ekart.dto.*;
import com.infy.ekart.entity.*;
import com.infy.ekart.enums.Role;
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
    //private final EmailService emailService;
    private final Map<String, String> otpStore = new HashMap<>();

    public AuthService(CustomerRepository repo,
                       VerificationTokenRepository tokenRepo,
                       PasswordEncoder encoder,
                       JwtUtil jwtUtil) {
        this.repo = repo;
        this.tokenRepo = tokenRepo;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
        //this.emailService = emailService;
    }

    // ==================== REGISTER ====================
    public ApiResponse register(CustomerDTO dto) {
        log.info("Registering user: {}", dto.getEmailId());

        if (repo.findByEmailId(dto.getEmailId()).isPresent()) {
            throw new RuntimeException("User already exists with email: " + dto.getEmailId());
        }

        Customer user = new Customer();
        user.setEmailId(dto.getEmailId());
        user.setName(dto.getName());
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setAddress(dto.getAddress());
        user.setVerified(false);

        // Set role from DTO or default to USER
        if (dto.getRole() != null && dto.getRole().equalsIgnoreCase("ADMIN")) {
            user.setRole(Role.ROLE_ADMIN);
        } else {
            user.setRole(Role.ROLE_USER);
        }

        repo.save(user);

        // Generate verification token
        String token = UUID.randomUUID().toString();
        VerificationToken vt = new VerificationToken();
        vt.setEmail(dto.getEmailId());
        vt.setToken(token);
        vt.setExpiryDate(new Date(System.currentTimeMillis() + 600000)); // 10 minutes
        tokenRepo.save(vt);

        // Send verification email (uncomment when ready)
        // emailService.sendEmail(dto.getEmailId(), token);

        log.info("User registered: {} with role: {}", dto.getEmailId(), user.getRole());
        return new ApiResponse("Registered successfully. Please verify your email.", true);
    }

    // ==================== LOGIN ====================
    public LoginResponse login(LoginRequest req) {
        log.info("Login attempt for: {}", req.email());

        // Find user
        Customer user = repo.findByEmailId(req.email())
            .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        // Verify password
        if (!encoder.matches(req.password(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        // Check if verified (optional)
        // if (!user.isVerified()) {
        //     throw new RuntimeException("Please verify your email first");
        // }

        // Generate JWT token with role
        String token = jwtUtil.generateToken(
            user.getEmailId(),
            user.getName()
        );

        log.info("User logged in: {} with role: {}", req.email(), user.getRole());

        // Return login response with role
        return new LoginResponse(
            token,                   // JWT token
            user.getEmailId(),       // email
            user.getName(),          // name
            user.getPhoneNumber(),   // phone
            user.getRole().toString(), // role
            3600                     // expiresIn (1 hour)
        );
    }

    // ==================== VERIFY EMAIL ====================
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

    // ==================== OTP ====================
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

    // ==================== FORGOT PASSWORD ====================
    public String forgotPassword(String email) {
        Customer user = repo.findByEmailId(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        String resetToken = UUID.randomUUID().toString();
        VerificationToken vt = new VerificationToken();
        vt.setEmail(email);
        vt.setToken(resetToken);
        vt.setExpiryDate(new Date(System.currentTimeMillis() + 600000)); // 10 minutes
        tokenRepo.save(vt);

        // Send reset email (uncomment when ready)
        // emailService.sendPasswordResetEmail(email, resetToken);

        log.info("Password reset token sent to: {}", email);
        return "Password reset link sent to your email";
    }

    // ==================== RESET PASSWORD ====================
    public String resetPassword(String token, String newPassword) {
        VerificationToken vt = tokenRepo.findByToken(token)
            .orElseThrow(() -> new RuntimeException("Invalid reset token"));

        if (vt.getExpiryDate().before(new Date())) {
            throw new RuntimeException("Reset token has expired");
        }

        Customer user = repo.findByEmailId(vt.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(encoder.encode(newPassword));
        repo.save(user);
        tokenRepo.delete(vt);

        log.info("Password reset for: {}", vt.getEmail());
        return "Password reset successfully";
    }

    // ==================== GET USER BY EMAIL ====================
    public LoginResponse getUserByEmail(String email) {
        Customer user = repo.findByEmailId(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        return new LoginResponse(
            null,
            user.getEmailId(),
            user.getName(),
            user.getPhoneNumber(),
            user.getRole().toString(),
            0
        );
    }

    // ==================== UPDATE USER ROLE (Admin) ====================
    public String updateUserRole(String email, String newRole) {
        Customer user = repo.findByEmailId(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        user.setRole(Role.valueOf(newRole));
        repo.save(user);

        log.info("User role updated: {} -> {}", email, newRole);
        return "User role updated to " + newRole;
    }
}