package com.infy.ekart.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CustomerDTO {
    @NotBlank(message = "Please provide a valid name")
    private String name;

    @NotBlank @Email(message = "Please provide a valid email")
    private String emailId;

    @NotBlank(message = "Please provide a valid password")
    private String password;

    private String phoneNumber;
    private String address;
    private String role;            // optional, defaults to USER in service
}