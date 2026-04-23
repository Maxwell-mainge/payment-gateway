package com.userservice.demo.otp.service;

import com.userservice.demo.otp.dto.VerifyOtpRequest;
import com.userservice.demo.otp.model.OtpRecord;
import com.userservice.demo.otp.repository.OtpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import com.userservice.demo.user.repository.CustomerRepository;
import com.userservice.demo.user.repository.MerchantRepository;
import com.userservice.demo.user.model.Customer;
import com.userservice.demo.user.model.Merchant;


/**
 * Service for generating and verifying OTPs.
 * Uses Redis for fast OTP storage with auto-expiry.
 * Uses database for audit trail of OTP usage.
 */
@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpRepository otpRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final CustomerRepository customerRepository;
    private final MerchantRepository merchantRepository;

    /** Email OTP expires in 24 hours */
    private static final long EMAIL_OTP_EXPIRY_MINUTES = 1440;

    /** Phone OTP expires in 10 minutes */
    private static final long PHONE_OTP_EXPIRY_MINUTES = 10;

    /**
     * Generates a 6 digit OTP and stores it in Redis and database.
     * Prints the OTP to console (mocking email/SMS sending).
     *
     * @param recipient the email or phone number to send OTP to
     * @param otpType   EMAIL or PHONE
     * @return the generated OTP code
     */
    public String generateOtp(String recipient, OtpRecord.OtpType otpType) {
        // Generate random 6 digit OTP
        String otpCode = String.format("%06d", new Random().nextInt(999999));

        // Determine expiry based on type
        long expiryMinutes = otpType == OtpRecord.OtpType.EMAIL
                ? EMAIL_OTP_EXPIRY_MINUTES
                : PHONE_OTP_EXPIRY_MINUTES;

        // Store OTP in Redis with expiry
        String redisKey = "otp:" + otpType.name().toLowerCase() + ":" + recipient;
        redisTemplate.opsForValue().set(redisKey, otpCode, expiryMinutes, TimeUnit.MINUTES);

        // Save OTP record in database for audit trail
        OtpRecord record = new OtpRecord();
        record.setRecipient(recipient);
        record.setOtpType(otpType);
        record.setStatus(OtpRecord.OtpStatus.PENDING);
        record.setExpiresAt(LocalDateTime.now().plusMinutes(expiryMinutes));
        otpRepository.save(record);

        // Mock sending - print to console instead of real email/SMS
        System.out.println("===========================================");
        System.out.println("OTP for " + otpType.name() + " - " + recipient + ": " + otpCode);
        System.out.println("===========================================");

        return otpCode;
    }

    /**
     * Verifies an OTP submitted by the user.
     * Checks Redis for the OTP and marks the database record as USED.
     *
     * @param request contains recipient, otpType and otpCode
     * @return true if OTP is valid, false otherwise
     */

    public boolean verifyOtp(VerifyOtpRequest request) {
        // Build Redis key
        String redisKey = "otp:" + request.getOtpType().toLowerCase() + ":" + request.getRecipient();

        // Get OTP from Redis
        String storedOtp = redisTemplate.opsForValue().get(redisKey);

        // Check if OTP exists and matches
        if (storedOtp == null) {
            throw new RuntimeException("OTP expired or not found");
        }
        if (!storedOtp.equals(request.getOtpCode())) {
            throw new RuntimeException("Invalid OTP");
        }

        // Delete OTP from Redis so it can't be reused
        redisTemplate.delete(redisKey);

        // Convert string to enum
        OtpRecord.OtpType otpType = OtpRecord.OtpType.valueOf(request.getOtpType().toUpperCase());

        System.out.println("Looking up customer for: " + request.getRecipient() + " type: " + otpType);

        // Check if both email and phone are verified and activate account
        if (otpType == OtpRecord.OtpType.EMAIL) {
            customerRepository.findByEmail(request.getRecipient()).ifPresent(customer -> {
                customer.setEmailVerified(true);
                if (customer.isPhoneVerified()) {
                    customer.setAccountStatus(Customer.AccountStatus.ACTIVE);
                }
                customerRepository.save(customer);
            });
            merchantRepository.findByEmail(request.getRecipient()).ifPresent(merchant -> {
                merchant.setEmailVerified(true);
                if (merchant.isPhoneVerified()) {
                    merchant.setAccountStatus(Merchant.AccountStatus.ACTIVE);
                }
                merchantRepository.save(merchant);
            });
        }

        if (otpType == OtpRecord.OtpType.PHONE) {
            customerRepository.findByPhoneNumber(request.getRecipient()).ifPresent(customer -> {
                customer.setPhoneVerified(true);
                if (customer.isEmailVerified()) {
                    customer.setAccountStatus(Customer.AccountStatus.ACTIVE);
                }
                customerRepository.save(customer);
            });
            merchantRepository.findByPhoneNumber(request.getRecipient()).ifPresent(merchant -> {
                merchant.setPhoneVerified(true);
                if (merchant.isEmailVerified()) {
                    merchant.setAccountStatus(Merchant.AccountStatus.ACTIVE);
                }
                merchantRepository.save(merchant);
            });
        }

        // Update database record status to USED
        otpRepository.findByRecipientAndOtpTypeAndStatus(
                request.getRecipient(),
                otpType,
                OtpRecord.OtpStatus.PENDING
        ).ifPresent(record -> {
            record.setStatus(OtpRecord.OtpStatus.USED);
            record.setResolvedAt(LocalDateTime.now());
            otpRepository.save(record);
        });

        return true;
    }
    /**
     * Resends an OTP to a recipient.
     * Deletes any existing OTP from Redis and generates a fresh one.
     * Updates the old database record to EXPIRED and creates a new one.
     *
     * @param recipient the email or phone number to resend OTP to
     * @param otpType   EMAIL or PHONE
     */
    public void resendOtp(String recipient, OtpRecord.OtpType otpType) {
        // Delete existing OTP from Redis if any
        String redisKey = "otp:" + otpType.name().toLowerCase() + ":" + recipient;
        redisTemplate.delete(redisKey);

        // Mark any existing PENDING record as EXPIRED
        otpRepository.findByRecipientAndOtpTypeAndStatus(
                recipient,
                otpType,
                OtpRecord.OtpStatus.PENDING
        ).ifPresent(record -> {
            record.setStatus(OtpRecord.OtpStatus.EXPIRED);
            record.setResolvedAt(LocalDateTime.now());
            otpRepository.save(record);
        });

        // Generate fresh OTP
        generateOtp(recipient, otpType);
    }
}