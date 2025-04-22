package com.ai.deepsight.service;

import org.springframework.stereotype.Service;

@Service
public class EmailService {
    public void sendVerificationEmail(String email, String token) {
        // Simulate sending email (log or print instead of actual email sending)
        System.out.println("Sending verification email to " + email + " with token: " + token);
    }
}
