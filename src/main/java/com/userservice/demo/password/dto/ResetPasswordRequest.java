package com.userservice.demo.password.dto;

import lombok.Data;

/**
 * DTO for resetting password using a reset token.
 * Used in the forgot password flow where no old password is required.
 */
@Data
public class ResetPasswordRequest {
    /** The reset token sent to the user's email */
    private String token;

    /** The new password to set */
    private String newPassword;
}