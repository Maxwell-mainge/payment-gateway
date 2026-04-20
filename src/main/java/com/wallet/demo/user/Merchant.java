package com.wallet.demo.user;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "merchants")
public class Merchant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String businessName;

    @Column(nullable = false, unique = true)
    private String businessRegistrationNumber;

    @Column(nullable = false, unique = true)
    private String businessKraPin;

    @Column(nullable = false)
    private String businessType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    @Column(nullable = false)
    private String bankName;

    @Column(nullable = false)
    private String bankAccountNumber;

    @Column(nullable = false)
    private String bankAccountHolderName;

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
}