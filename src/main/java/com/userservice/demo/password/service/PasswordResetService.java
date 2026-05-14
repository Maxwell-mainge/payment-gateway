package com.userservice.demo.password.service;

import com.userservice.demo.auth.repository.AuthUserRepository;
import com.userservice.demo.exception.BadRequestException;
import com.userservice.demo.exception.ResourceNotFoundException;
import com.userservice.demo.password.dto.ChangePasswordRequest;
import com.userservice.demo.password.dto.ForgotPasswordRequest;
import com.userservice.demo.password.dto.ResetPasswordRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Service for handling password reset and change operations.
 * Uses Redis to store reset tokens with configurable expiry.
 * Password updates go through AuthUser since auth is centralized.
 */
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final AuthUserRepository authUserRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.reset-token-expiry-days}")
    private int resetTokenExpiryDays;

    /**
     * Generates a password reset token for the given email.
     * Stores token in Redis with expiry and prints reset link to console.
     */
    public void forgotPassword(ForgotPasswordRequest request) {
        String email = request.getEmail();

        if (!authUserRepository.existsByEmail(email)) {
            throw new ResourceNotFoundException("No account found with this email");
        }

        String resetToken = UUID.randomUUID().toString();
        String redisKey = "reset:password:" + email;
        redisTemplate.opsForValue().set(redisKey, resetToken, resetTokenExpiryDays, TimeUnit.DAYS);

        System.out.println("===========================================");
        System.out.println("Password reset link for: " + email);
        System.out.println("Token: " + resetToken);
        System.out.println("Link: http://localhost:8080/api/password/reset?token=" + resetToken + "&email=" + email);
        System.out.println("===========================================");
    }

    /**
     * Resets password using email and reset token from request body.
     * Updates password on AuthUser since auth is centralized.
     */
    public void resetPassword(ResetPasswordRequest request) {
        String email = request.getEmail();

        String redisKey = "reset:password:" + email;
        String storedToken = redisTemplate.opsForValue().get(redisKey);

        if (storedToken == null) {
            throw new BadRequestException("Reset token expired or not found");
        }
        if (!storedToken.equals(request.getToken())) {
            throw new BadRequestException("Invalid reset token");
        }

        redisTemplate.delete(redisKey);

        authUserRepository.findByEmail(email).ifPresent(authUser -> {
            authUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
            authUserRepository.save(authUser);
        });
    }

    /**
     * Changes password for a logged-in user.
     * Requires old password verification before updating.
     * Updates password on AuthUser since auth is centralized.
     */
    public void changePassword(String email, ChangePasswordRequest request) {
        var authUser = authUserRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), authUser.getPassword())) {
            throw new BadRequestException("Old password is incorrect");
        }

        authUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        authUserRepository.save(authUser);
    }
}