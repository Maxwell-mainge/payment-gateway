package com.userservice.demo.password.dto;

import lombok.Data;

/**
 * DTO for forgot password requests.
 * User submits their email to receive a password reset token.
 */
@Data
public class ForgotPasswordRequest {
    /** The email address of the account to reset */
    private String email;
}