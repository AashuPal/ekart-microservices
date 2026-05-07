package com.infy.ekart.notificationservice.dto.response;

public class EmailResponse {

    private boolean success;
    private String message;

    public EmailResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
}