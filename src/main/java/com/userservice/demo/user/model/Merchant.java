package com.userservice.demo.user.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "merchants")
public class Merchant {

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

    @Column(nullable = false)
    private String businessName;

    @Column(nullable = false, unique = true)
    private String businessRegistrationNumber;

    @Column(nullable = false, unique = true)
    private String businessKraPin;

    @Column(nullable = false)
    private String businessType;

    @Column(nullable = false)
    private String bankName;

    @Column(nullable = false)
    private String bankAccountNumber;

    @Column(nullable = false)
    private String bankAccountHolderName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

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

    public enum VerificationStatus {
        PENDING, VERIFIED, REJECTED
    }

    public enum AccountStatus {
        PENDING, ACTIVE, SUSPENDED, CLOSED
    }
}