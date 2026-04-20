package com.wallet.demo.user;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String nationalId;

    @Column(nullable = false, unique = true)
    private String kraPin;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus accountStatus = AccountStatus.PENDING;

    private boolean emailVerified = false;
    private boolean phoneVerified = false;

    private int failedLoginAttempts = 0;
    private LocalDateTime lastLoginAt;

    private boolean termsAccepted = false;
    private LocalDateTime termsAcceptedAt;

    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum Role {
        CUSTOMER, MERCHANT, ADMIN
    }

    public enum AccountStatus {
        PENDING, ACTIVE, SUSPENDED, CLOSED
    }
}