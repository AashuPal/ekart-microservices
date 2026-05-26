package com.infy.ekart.service;

import com.infy.ekart.dto.*;
import java.util.Objects;
import com.infy.ekart.entity.*;
import com.infy.ekart.enums.Role;
import com.infy.ekart.exception.AuthException;
import com.infy.ekart.repository.CustomerRepository;
import com.infy.ekart.repository.VerificationTokenRepository;
import com.infy.ekart.security.JwtUtil;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.*;

@Service
@Transactional
@Slf4j
public class AuthService {

	private final CustomerRepository repo;
	private final VerificationTokenRepository tokenRepo;
	private final PasswordEncoder encoder;
	private final JwtUtil jwtUtil;
	private final EmailService emailService;
	private final Map<String, OtpData> otpStore = new ConcurrentHashMap<>();
	private static final long OTP_EXPIRY_SECONDS = 3600;

	@Value("${twilio.account-sid}")
	private String twilioAccountSid;

	@Value("${twilio.auth-token}")
	private String twilioAuthToken;

	@Value("${twilio.phone-number}")
	private String twilioPhoneNumber;

	@Value("${google.client-id}")
	private String googleClientId;

	@Value("${google.client-secret}")
	private String googleClientSecret;

	@Value("${google.redirect-uri}")
	private String googleRedirectUri;

	@PostConstruct
	public void initTwilio() {
		Twilio.init(twilioAccountSid, twilioAuthToken);
	}

	public AuthService(CustomerRepository repo, VerificationTokenRepository tokenRepo, PasswordEncoder encoder,
			JwtUtil jwtUtil, EmailService emailService) {
		this.repo = repo;
		this.tokenRepo = tokenRepo;
		this.encoder = encoder;
		this.jwtUtil = jwtUtil;
		this.emailService = emailService;
	}

	// ---------- REGISTER (LOCAL, EMAIL VERIFICATION LINK) ----------
	public ApiResponse register(CustomerDTO dto) {
		if (repo.findByEmailId(dto.getEmailId()).isPresent()) {
			throw new AuthException("User already exists with email: " + dto.getEmailId());
		}

		Customer customer = Customer.builder().emailId(dto.getEmailId()).name(dto.getName())
				.password(encoder.encode(dto.getPassword())).phoneNumber(dto.getPhoneNumber()).address(dto.getAddress())
				.role("ADMIN".equalsIgnoreCase(dto.getRole()) ? Role.ROLE_ADMIN : Role.ROLE_USER).provider("LOCAL")
				.isVerified(false).build();
		repo.save(customer);

		// Generate verification token
		String token = UUID.randomUUID().toString();
		VerificationToken vt = VerificationToken.builder().email(dto.getEmailId()).token(token)
				.expiryDate(Date.from(Instant.now().plusSeconds(600))) // 10 minutes
				.build();
		tokenRepo.save(vt);

		// Send verification link via email
		emailService.sendVerificationLink(dto.getEmailId(), dto.getName(), token);

		log.info("User registered: {}", dto.getEmailId());
		return new ApiResponse("Registered! Check your email to verify your account.", true);
	}

	// ---------- LOGIN (LOCAL) ----------
	public LoginResponse login(LoginRequest req) {
		Customer customer = repo.findByEmailId(req.email())
				.orElseThrow(() -> new AuthException("Invalid email or password"));

		// Check if the user registered via phone or Google (no password set)
		if (customer.getPassword() == null) {
			throw new AuthException("This account was created via " + customer.getProvider()
					+ ". Please use that login method or set a password via 'Forgot Password'.");
		}

		if (!encoder.matches(req.password(), customer.getPassword())) {
			throw new AuthException("Invalid email or password");
		}

		if (!customer.isVerified()) {
			throw new AuthException("Email not verified. Check your inbox.");
		}

		String token = jwtUtil.generateToken(customer.getEmailId(), customer.getRole().toString(), customer.getName());
		log.info("User logged in: {}", req.email());
		return new LoginResponse(token, customer.getEmailId(), customer.getName(), customer.getPhoneNumber(),
				customer.getRole().toString(), 3600);
	}

	// ---------- EMAIL VERIFICATION (from link) ----------
	public String verifyEmail(String token) {
		VerificationToken vt = tokenRepo.findByToken(token)
				.orElseThrow(() -> new AuthException("Invalid verification token"));
		if (vt.getExpiryDate().before(new Date())) {
			tokenRepo.delete(vt);
			throw new AuthException("Verification token expired. Request a new one.");
		}
		Customer customer = repo.findByEmailId(vt.getEmail()).orElseThrow(() -> new AuthException("User not found"));
		customer.setVerified(true);
		repo.save(customer);
		tokenRepo.delete(vt);
		log.info("Email verified for: {}", vt.getEmail());
		return "Email verified successfully! You can now login.";
	}

	// ---------- RESEND VERIFICATION LINK ----------
	public ApiResponse resendVerification(String email) {
		Customer user = repo.findByEmailId(email).orElseThrow(() -> new AuthException("User not found"));

		if (user.isVerified()) {
			return new ApiResponse("Email already verified", true);
		}

		// Generate new token and save
		String token = UUID.randomUUID().toString();
		VerificationToken vt = VerificationToken.builder().email(email).token(token)
				.expiryDate(Date.from(Instant.now().plusSeconds(600))).build();
		tokenRepo.save(vt);

		// Send verification link
		emailService.sendVerificationLink(email, user.getName(), token);

		log.info("Verification link resent to {}", email);
		return new ApiResponse("Verification link resent to your email", true);
	}

	public LoginResponse googleLogin(String authorizationCode) {
		log.info("Starting Google OAuth login with code: {}",
				authorizationCode != null ? authorizationCode.substring(0, 10) + "..." : "null");

		try {
			// Step 1: Exchange authorization code for tokens
			RestTemplate rest = new RestTemplate();

			String tokenRequestBody = "code=" + authorizationCode + "&client_id=" + googleClientId + "&client_secret="
					+ googleClientSecret + "&redirect_uri="
					+ URLEncoder.encode(googleRedirectUri, StandardCharsets.UTF_8) + "&grant_type=authorization_code";

			HttpHeaders tokenHeaders = new HttpHeaders();
			tokenHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

			log.debug("Token request body: {}", tokenRequestBody);

			ResponseEntity<Map> tokenResponse = rest.exchange("https://oauth2.googleapis.com/token", HttpMethod.POST,
					new HttpEntity<>(tokenRequestBody, tokenHeaders), Map.class);

			log.info("Google token response status: {}", tokenResponse.getStatusCode());

			if (tokenResponse.getStatusCode() != HttpStatus.OK || tokenResponse.getBody() == null) {
				log.error("Google token exchange failed: {}", tokenResponse.getBody());
				throw new AuthException("Failed to get access token from Google");
			}

			Map<String, Object> tokenData = tokenResponse.getBody();

			// Check for error in response
			if (tokenData.containsKey("error")) {
				log.error("Google token error: {} - {}", tokenData.get("error"), tokenData.get("error_description"));
				throw new AuthException("Google authentication failed: " + tokenData.get("error_description"));
			}

			String accessToken = (String) tokenData.get("access_token");
			log.info("Access token obtained successfully");

			// Step 2: Get user info from Google
			HttpHeaders userHeaders = new HttpHeaders();
			userHeaders.setBearerAuth(accessToken);

			ResponseEntity<Map> userResponse = rest.exchange("https://www.googleapis.com/oauth2/v3/userinfo",
					HttpMethod.GET, new HttpEntity<>(userHeaders), Map.class);

			Map<String, Object> googleUser = userResponse.getBody();
			if (googleUser == null) {
				throw new AuthException("Failed to get user info from Google");
			}

			String email = (String) googleUser.get("email");
			String name = (String) googleUser.getOrDefault("name", "User");
			boolean emailVerified = Boolean.TRUE.equals(googleUser.get("email_verified"));

			log.info("Google user info - Email: {}, Name: {}, Verified: {}", email, name, emailVerified);

			if (email == null || email.isEmpty()) {
				throw new AuthException("Email not provided by Google");
			}

			// Step 3: Find or create user in database
			Customer customer = repo.findByEmailId(email).orElse(null);

			if (customer == null) {
				// New Google user
				customer = new Customer();
				customer.setEmailId(email);
				customer.setName(name);
				customer.setProvider("GOOGLE");
				customer.setVerified(emailVerified);
				customer.setRole(Role.ROLE_USER);
				customer.setPassword(null); // Google users don't have local password
				customer = repo.save(customer);
				log.info("New Google user created: {}", email);
			} else {
				// Existing user - update if needed
				if (!customer.isVerified() && emailVerified) {
					customer.setVerified(true);
					repo.save(customer);
				}
				log.info("Existing user logged in via Google: {}", email);
			}

			// Step 4: Generate JWT token
			String jwtToken = jwtUtil.generateToken(customer.getEmailId(), customer.getRole().toString(),
					customer.getName() != null ? customer.getName() : name);

			log.info("Google login successful for: {}", email);

			return new LoginResponse(jwtToken, customer.getEmailId(), customer.getName(), customer.getPhoneNumber(),
					customer.getRole().toString(), 3600);

		} catch (AuthException e) {
			log.error("Google login AuthException: {}", e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("Google login unexpected error: {}", e.getMessage(), e);
			throw new AuthException("Google login failed: " + e.getMessage());
		}
	}

	private String normalizePhone(String phone) {
		if (phone == null)
			return "";
		// Keep leading '+' and digits only
		if (phone.startsWith("+")) {
			return "+" + phone.substring(1).replaceAll("[^0-9]", "");
		}
		String digits = phone.replaceAll("[^0-9]", "");
		// India: if exactly 10 digits, add +91
		if (digits.length() == 10) {
			return "+91" + digits;
		}
		// Already has country code (e.g. 91xxxxxxxxxx)
		if (digits.length() == 12 && digits.startsWith("91")) {
			return "+" + digits;
		}
		// Fallback – assume it’s already a full number with country code
		return "+" + digits;
	}

	// ---------- SEND OTP (Twilio SMS) ----------
	public String sendOtp(String phoneNumber) {
		String cleanPhone = normalizePhone(phoneNumber);
		String otp = String.format("%06d", new Random().nextInt(900000) + 100000);

		otpStore.put(cleanPhone, new OtpData(otp, System.currentTimeMillis()));

		// Send via Twilio (keep your existing code)
		Message.creator(new PhoneNumber(phoneNumber), new PhoneNumber(twilioPhoneNumber),
				"Your eKart verification code is: " + otp).create();

		// Temporary debug log – remove in production
		log.info("Generated OTP for {} : {}", cleanPhone, otp);

		log.info("OTP sent to {}", phoneNumber);
		return "OTP sent successfully.";
	}

	// ---------- VERIFY OTP ----------
	public LoginResponse verifyOtp(String phoneNumber, String otp) {
		String cleanPhone = normalizePhone(phoneNumber);

		try {
			// 1. Validate OTP
			OtpData stored = otpStore.get(cleanPhone);
			if (stored == null) {
				throw new AuthException("Invalid or expired OTP");
			}

			long elapsed = (System.currentTimeMillis() - stored.createdAt) / 1000;
			if (elapsed > OTP_EXPIRY_SECONDS) {
				otpStore.remove(cleanPhone);
				throw new AuthException("OTP has expired");
			}

			if (!stored.otp.equals(otp)) {
				throw new AuthException("Invalid OTP");
			}

			// OTP valid – consume it
			otpStore.remove(cleanPhone);

			// 2. Find existing customer OR create a new one
			Customer customer = repo.findByPhoneNumberAndProvider(cleanPhone, "PHONE").orElseGet(() -> {
				// --- New user: phone‑only account ---
				Customer newUser = new Customer();
				newUser.setEmailId(cleanPhone + "@phone.ekart");
				newUser.setPhoneNumber(cleanPhone);
				newUser.setProvider("PHONE");
				newUser.setVerified(true);
				newUser.setRole(Role.ROLE_USER);
				newUser.setName("User" + cleanPhone.substring(cleanPhone.length() - 4));
				newUser.setPassword(null); // no password for phone users
				return repo.save(newUser);
			});

			// 3. Generate JWT
			String name = customer.getName() != null ? customer.getName() : "User";
			String token = jwtUtil.generateToken(customer.getEmailId(), customer.getRole().toString(), name);

			log.info("User logged in via OTP: {}", cleanPhone);

			return new LoginResponse(token, customer.getEmailId(), customer.getName(), customer.getPhoneNumber(),
					customer.getRole().toString(), 3600);

		} catch (AuthException e) {
			throw e;
		} catch (Exception e) {
			log.error("OTP verification failed for {}: {}", cleanPhone, e.getMessage(), e);
			throw new RuntimeException("Internal server error", e);
		}
	}

	// ---------- FORGOT PASSWORD ----------
	public ApiResponse forgotPassword(String email) {
		Customer customer = repo.findByEmailId(email)
				.orElseThrow(() -> new AuthException("No account found with email: " + email));

		String resetToken = UUID.randomUUID().toString();
		VerificationToken vt = VerificationToken.builder().email(email).token(resetToken)
				.expiryDate(Date.from(Instant.now().plusSeconds(600))).build();
		tokenRepo.save(vt);
		emailService.sendPasswordResetEmail(email, resetToken);

		return new ApiResponse("Password reset link sent to your email.", true);
	}

	// ---------- RESET PASSWORD (with token) ----------
	public ApiResponse resetPassword(String token, String newPassword) {
		VerificationToken vt = tokenRepo.findByToken(token).orElseThrow(() -> new AuthException("Invalid reset token"));
		if (vt.getExpiryDate().before(new Date())) {
			tokenRepo.delete(vt);
			throw new AuthException("Reset token expired.");
		}
		Customer customer = repo.findByEmailId(vt.getEmail()).orElseThrow(() -> new AuthException("User not found"));
		customer.setPassword(encoder.encode(newPassword));
		repo.save(customer);
		tokenRepo.delete(vt);
		return new ApiResponse("Password reset successfully.", true);
	}

	// ---------- CHANGE PASSWORD (logged in) ----------
	public ApiResponse changePassword(String email, String oldPassword, String newPassword) {
		Customer customer = repo.findByEmailId(email).orElseThrow(() -> new AuthException("User not found"));

		if (!encoder.matches(oldPassword, customer.getPassword())) {
			throw new AuthException("Current password is incorrect");
		}
		customer.setPassword(encoder.encode(newPassword));
		repo.save(customer);
		log.info("Password changed for: {}", email);
		return new ApiResponse("Password changed successfully.", true);
	}

	// ---------- UPDATE USER ROLE (Super Admin only) ----------
	public String updateUserRole(String targetEmail, String newRole, String requesterEmail) {
		Customer requester = repo.findByEmailId(requesterEmail)
				.orElseThrow(() -> new AuthException("Requester not found"));
		if (requester.getRole() != Role.ROLE_ADMIN) {
			throw new AuthException("Only Super Admin can change roles");
		}
		Customer target = repo.findByEmailId(targetEmail).orElseThrow(() -> new AuthException("Target user not found"));
		target.setRole(Role.valueOf(newRole));
		repo.save(target);
		log.info("Role updated: {} -> {} by {}", targetEmail, newRole, requesterEmail);
		return "User role updated to " + newRole;
	}

	// ---------- DELETE USER (Super Admin only) ----------
	public ApiResponse deleteUser(String email, String requesterEmail) {
		Customer requester = repo.findByEmailId(requesterEmail)
				.orElseThrow(() -> new AuthException("Requester not found"));
		if (requester.getRole() != Role.ROLE_ADMIN) {
			throw new AuthException("Only Super Admin can delete users");
		}
		Customer target = repo.findByEmailId(email).orElseThrow(() -> new AuthException("User not found"));
		repo.delete(target);
		log.info("User deleted: {} by {}", email, requesterEmail);
		return new ApiResponse("User deleted successfully", true);
	}

	// ---------- GET ALL USERS ----------
	public List<LoginResponse> getAllUsers() {
		return repo.findAll().stream().map(user -> new LoginResponse(null, user.getEmailId(), user.getName(),
				user.getPhoneNumber(), user.getRole().toString(), 0)).toList();
	}

	public LoginResponse getUserByEmail(String email) {
		Customer user = repo.findByEmailId(email).orElseThrow(() -> new AuthException("User not found"));
		return new LoginResponse(null, user.getEmailId(), user.getName(), user.getPhoneNumber(),
				user.getRole().toString(), 0);
	}

	// Update profile
	public ApiResponse updateProfile(String emailId, UpdateProfileRequest request) {
	    Customer customer = repo.findByEmailId(emailId)
	            .orElseThrow(() -> new AuthException("User not found"));

	    // ----- NAME -----
	    customer.setName(request.getName());

	    // ----- PHONE -----
	    if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) {
	        String normalized = normalizePhone(request.getPhoneNumber());
	        if (!normalized.equals(customer.getPhoneNumber())) {
	            repo.findByPhoneNumber(normalized).ifPresent(existing -> {
	                if (!existing.getEmailId().equals(emailId))
	                    throw new AuthException("Phone number already in use");
	            });
	            customer.setPhoneNumber(normalized);
	        }
	    }

	    // ----- ADDRESS -----
	    if (request.getAddress() != null) {
	        customer.setAddress(request.getAddress());
	    }

	    // ----- VERIFIED EMAIL (only for PHONE) -----
	    if (request.getVerifiedEmail() != null && !request.getVerifiedEmail().isBlank()) {
	        if (!"PHONE".equals(customer.getProvider())) {
	            throw new AuthException("Email can only be set for phone‑based accounts");
	        }

	        String newEmail = request.getVerifiedEmail().trim().toLowerCase();

	        // Null‑safe comparison – prevents NPE
	        if (!Objects.equals(newEmail, customer.getVerifiedEmail())) {
	            // Check uniqueness
	            Optional<Customer> existing = repo.findByVerifiedEmail(newEmail);
	            if (existing.isPresent() && !existing.get().getEmailId().equals(emailId)) {
	                throw new AuthException("Email already in use");
	            }
	            customer.setVerifiedEmail(newEmail);
	            log.info("Verified email set to {} for user {}", newEmail, emailId);
	        } else {
	            log.info("Verified email unchanged for user {}", emailId);
	        }
	    }

	    repo.save(customer); // flush changes
	    log.info("Profile updated for: {}", emailId);
	    return new ApiResponse("Profile updated successfully", true);
	}
	private String generateEmailVerificationToken(Customer customer) {
	    String token = UUID.randomUUID().toString();
	    VerificationToken vt = VerificationToken.builder()
	            .email(customer.getEmailId())   // or use the new verifiedEmail
	            .token(token)
	            .expiryDate(Date.from(Instant.now().plusSeconds(600)))
	            .build();
	    tokenRepo.save(vt);
	    return token;
	}

	private static class OtpData {
		final String otp;
		final long createdAt;

		OtpData(String otp, long createdAt) {
			this.otp = otp;
			this.createdAt = createdAt;
		}
	}
}