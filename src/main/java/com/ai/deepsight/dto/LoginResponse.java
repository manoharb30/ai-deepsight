package com.ai.deepsight.dto;

public class LoginResponse {
    private String token;
    private boolean success;

    public LoginResponse(String token, boolean success) {
        this.token = token;
        this.success = success;
    }

    public String getToken() { return token; }
    public boolean isSuccess() { return success; }
}
