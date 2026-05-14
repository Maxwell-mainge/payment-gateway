package com.userservice.demo.password.dto;

import lombok.Data;

/**
 * DTO for resetting password using a reset token.
 * Email is included in the request body instead of query parameter.
 */
@Data
public class ResetPasswordRequest {
    /** The email address of the account to reset */
    private String email;

    /** The reset token sent to the user's email */
    private String token;

    /** The new password to set */
    private String newPassword;
}