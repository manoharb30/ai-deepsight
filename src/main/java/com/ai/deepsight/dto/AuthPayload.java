package com.ai.deepsight.dto;

import com.ai.deepsight.model.User;

public class AuthPayload {
    private String token;
    private User user;

    public AuthPayload(String token, User user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() { return token; }
    public User getUser() { return user; }
}
