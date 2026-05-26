package com.infy.ekart.entity;

import com.infy.ekart.enums.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customer")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @Column(length = 100)
    private String emailId;

    private String name;
    private String password;
    @Column(unique = true)
    private String phoneNumber;
    private String address;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.ROLE_USER;

    @Builder.Default
    private boolean isVerified = false;

    @Column(length = 20)
    private String provider;              // LOCAL, GOOGLE, PHONE
    
    @Column(unique = true, nullable = true)
    private String verifiedEmail;          
}