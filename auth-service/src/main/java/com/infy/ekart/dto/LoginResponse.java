package com.infy.ekart.dto;

public class LoginResponse {
    private String token;
    private String email;
    private String name;
    private Long phoneNumber;
    private long expiresIn;

    public LoginResponse(String token, String email, String name, Long phoneNumber, long expiresIn) {
        this.token = token;
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.expiresIn = expiresIn;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(Long phoneNumber) { this.phoneNumber = phoneNumber; }

    public long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(long expiresIn) { this.expiresIn = expiresIn; }
}