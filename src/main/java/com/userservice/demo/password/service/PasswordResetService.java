package com.userservice.demo.password.service;

import com.userservice.demo.password.dto.ChangePasswordRequest;
import com.userservice.demo.password.dto.ForgotPasswordRequest;
import com.userservice.demo.password.dto.ResetPasswordRequest;
import com.userservice.demo.user.model.Customer;
import com.userservice.demo.user.model.Merchant;
import com.userservice.demo.user.repository.CustomerRepository;
import com.userservice.demo.user.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Service for handling password reset and change operations.
 * Uses Redis to store reset tokens with configurable expiry.
 */
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final CustomerRepository customerRepository;
    private final MerchantRepository merchantRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final PasswordEncoder passwordEncoder;

    /** Reset token expiry in days - configured in application.yaml */
    @Value("${app.reset-token-expiry-days}")
    private int resetTokenExpiryDays;

    /**
     * Generates a password reset token for the given email.
     * Stores token in Redis with expiry and prints reset link to console.
     * Works for both customers and merchants.
     *
     * @param request contains the email to send reset token to
     */
    public void forgotPassword(ForgotPasswordRequest request) {
        String email = request.getEmail();

        // Check if email belongs to a customer or merchant
        boolean exists = customerRepository.existsByEmail(email)
                || merchantRepository.existsByEmail(email);

        if (!exists) {
            throw new RuntimeException("No account found with this email");
        }

        // Generate unique reset token
        String resetToken = UUID.randomUUID().toString();

        // Store token in Redis with expiry
        String redisKey = "reset:password:" + email;
        redisTemplate.opsForValue().set(
                redisKey,
                resetToken,
                resetTokenExpiryDays,
                TimeUnit.DAYS
        );

        // Mock sending reset link - print to console
        System.out.println("===========================================");
        System.out.println("Password reset link for: " + email);
        System.out.println("Token: " + resetToken);
        System.out.println("Link: http://localhost:8080/api/password/reset?token=" + resetToken + "&email=" + email);
        System.out.println("===========================================");
    }

    /**
     * Resets password using a valid reset token.
     * No old password required - used in forgot password flow.
     *
     * @param request contains the reset token and new password
     */
    public void resetPassword(ResetPasswordRequest request) {
        // Find which email this token belongs to
        // We store token by email so we need email in request
        throw new RuntimeException("Email is required for password reset");
    }

    /**
     * Resets password using email and reset token.
     *
     * @param email the email address of the account
     * @param request contains token and new password
     */
    public void resetPassword(String email, ResetPasswordRequest request) {
        // Get token from Redis
        String redisKey = "reset:password:" + email;
        String storedToken = redisTemplate.opsForValue().get(redisKey);

        if (storedToken == null) {
            throw new RuntimeException("Reset token expired or not found");
        }
        if (!storedToken.equals(request.getToken())) {
            throw new RuntimeException("Invalid reset token");
        }

        // Delete token from Redis so it can't be reused
        redisTemplate.delete(redisKey);

        // Update password for customer
        customerRepository.findByEmail(email).ifPresent(customer -> {
            customer.setPassword(passwordEncoder.encode(request.getNewPassword()));
            customerRepository.save(customer);
        });

        // Update password for merchant
        merchantRepository.findByEmail(email).ifPresent(merchant -> {
            merchant.setPassword(passwordEncoder.encode(request.getNewPassword()));
            merchantRepository.save(merchant);
        });
    }

    /**
     * Changes password for a logged-in user.
     * Requires old password verification before updating.
     *
     * @param email   the email of the logged-in user
     * @param request contains old and new password
     */
    public void changePassword(String email, ChangePasswordRequest request) {
        // Check customers
        Optional<Customer> customer = customerRepository.findByEmail(email);
        if (customer.isPresent()) {
            if (!passwordEncoder.matches(request.getOldPassword(), customer.get().getPassword())) {
                throw new RuntimeException("Old password is incorrect");
            }
            customer.get().setPassword(passwordEncoder.encode(request.getNewPassword()));
            customerRepository.save(customer.get());
            return;
        }

        // Check merchants
        Optional<Merchant> merchant = merchantRepository.findByEmail(email);
        if (merchant.isPresent()) {
            if (!passwordEncoder.matches(request.getOldPassword(), merchant.get().getPassword())) {
                throw new RuntimeException("Old password is incorrect");
            }
            merchant.get().setPassword(passwordEncoder.encode(request.getNewPassword()));
            merchantRepository.save(merchant.get());
            return;
        }

        throw new RuntimeException("User not found");
    }
}