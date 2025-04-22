package com.ai.deepsight.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    private Boolean isEmailVerified;
    private String verificationToken;
    private LocalDateTime verificationTokenExpiry;

    private String sessionId;

    private Integer credits;
    private Boolean subscriptionActive;
    private String planType; // FREE, PRO, ENTERPRISE

    private String preferredModel; // gpt-3.5-turbo etc.
    private String profileImageUrl;
    private String phone;
    private String organization;
    private String address;
    private String timezone;

    private Boolean isActive;

    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.isActive == null) this.isActive = false;
        if (this.isEmailVerified == null) this.isEmailVerified = false;
        this.credits = this.credits == null ? 0 : this.credits;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
