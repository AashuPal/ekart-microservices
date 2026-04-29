package com.infy.ekart.config;

import com.infy.ekart.entity.Customer;
import com.infy.ekart.enums.Role;
import com.infy.ekart.repository.CustomerRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.admin.name}")
    private String adminName;

    @Value("${app.admin.phone:}")
    private String adminPhone;

    @Value("${app.admin.role:ROLE_ADMIN}")
    private String adminRole;

    @Value("${app.default-user.email:user@ekart.com}")
    private String userEmail;

    @Value("${app.default-user.password:User@123}")
    private String userPassword;

    @Value("${app.default-user.name:Test User}")
    private String userName;

    @Value("${app.default-user.role:ROLE_USER}")
    private String userRole;

    public DataSeeder(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Create Admin
        if (customerRepository.findByEmailId(adminEmail).isEmpty()) {
            Customer admin = new Customer();
            admin.setEmailId(adminEmail);
            admin.setName(adminName);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setPhoneNumber(adminPhone);
            admin.setRole(Role.valueOf(adminRole));
            admin.setVerified(true);
            customerRepository.save(admin);
            log.info("✅ Admin created: {} with role: {}", adminEmail, adminRole);
        } else {
            log.info("Admin already exists: {}", adminEmail);
        }

        // Create Default User
        if (customerRepository.findByEmailId(userEmail).isEmpty()) {
            Customer user = new Customer();
            user.setEmailId(userEmail);
            user.setName(userName);
            user.setPassword(passwordEncoder.encode(userPassword));
            user.setRole(Role.valueOf(userRole));
            user.setVerified(true);
            customerRepository.save(user);
            log.info("✅ User created: {} with role: {}", userEmail, userRole);
        } else {
            log.info("User already exists: {}", userEmail);
        }
    }
}