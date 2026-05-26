package com.userservice.demo.password.dto;

import lombok.Data;

/**
 * DTO for changing password when logged in.
 * Requires old password verification before updating.
 */
@Data
public class ChangePasswordRequest {
    /** The user's current password for verification */
    private String oldPassword;

    /** The new password to set */
    private String newPassword;
}