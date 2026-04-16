package com.infy.ekart.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "EK_CUSTOMER")
@Data
public class Customer {

    @Id
    private String emailId;

    private String name;
    private String password;
    private Long phoneNumber;
    private String address;

    private boolean isVerified;
}