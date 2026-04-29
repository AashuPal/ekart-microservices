package com.infy.ekart.entity;

import com.infy.ekart.enums.Role;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "ek_customer")
public class Customer {

    @Id
    @Column(name = "email_id")
    private String emailId;

    @Column(name = "name")
    private String name;

    @Column(name = "password")
    private String password;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "address")
    private String address;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role role = Role.ROLE_USER;  // Default: USER

    // Getters and Setters
    public String getEmailId() { return emailId; }
    public void setEmailId(String emailId) { this.emailId = emailId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Boolean isVerified() { return isVerified; }
    public void setVerified(Boolean verified) { isVerified = verified; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}