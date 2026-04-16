package com.infy.ekart.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CustomerDTO {

    @Email
    @NotBlank
    private String emailId;

    @NotBlank
    private String name;

    @Size(min = 6)
    private String password;

    @NotNull
    private Long phoneNumber;

    @NotBlank
    private String address;
}