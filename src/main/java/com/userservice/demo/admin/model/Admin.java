package com.userservice.demo.admin.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Represents the admin account in the system.
 * Only one admin account exists, created on app startup.
 */
@Data
@Entity
@Table(name = "admins")
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Admin's full name */
    @Column(nullable = false)
    private String fullName;

    /** Admin's email - used for login */
    @Column(nullable = false, unique = true)
    private String email;

    /** Admin's encrypted password */
    @Column(nullable = false)
    private String password;

    /** Timestamp when admin account was created */
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}