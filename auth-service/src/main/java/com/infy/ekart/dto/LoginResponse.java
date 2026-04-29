package com.infy.ekart.dto;

public class LoginResponse {
    private String token;
    private String email;
    private String name;
    private String phoneNumber;
    private String role;  // ✅ Add role
    private long expiresIn;

    public LoginResponse(String token, String email, String name, 
                         String phoneNumber, String role, long expiresIn) {
        this.token = token;
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.expiresIn = expiresIn;
    }

    // Getters
    public String getToken() { return token; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getRole() { return role; }
    public long getExpiresIn() { return expiresIn; }
}