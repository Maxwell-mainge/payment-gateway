package com.userservice.demo.password.controller;

import com.userservice.demo.password.dto.ChangePasswordRequest;
import com.userservice.demo.password.dto.ForgotPasswordRequest;
import com.userservice.demo.password.dto.ResetPasswordRequest;
import com.userservice.demo.password.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for password management endpoints.
 * Handles forgot password, reset password and change password flows.
 */
@RestController
@RequestMapping("/api/password")
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    /**
     * Accepts an email and sends a password reset token.
     * Public endpoint - no authentication required.
     */
    @PostMapping("/forgot")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        passwordResetService.forgotPassword(request);
        return ResponseEntity.ok("Password reset link sent to your email");
    }

    /**
     * Resets password using a valid reset token.
     * Public endpoint - no authentication required.
     * Email included in request body.
     */
    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request);
        return ResponseEntity.ok("Password reset successfully");
    }
    /**
     * Changes password for a logged-in user.
     * Requires valid JWT token - protected endpoint.
     * Gets email from the JWT token via @AuthenticationPrincipal.
     */
    @PostMapping("/change")
    public ResponseEntity<String> changePassword(
            @AuthenticationPrincipal String email,
            @RequestBody ChangePasswordRequest request) {
        passwordResetService.changePassword(email, request);
        return ResponseEntity.ok("Password changed successfully");
    }
}