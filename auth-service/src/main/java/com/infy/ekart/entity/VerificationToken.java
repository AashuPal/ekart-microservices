package com.infy.ekart.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class VerificationToken {

    @Id
    @GeneratedValue
    private Long id;

    private String email;
    private String token;
    private Date expiryDate;
}