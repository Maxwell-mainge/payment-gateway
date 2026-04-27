package com.userservice.demo.profile.dto;

import lombok.Data;
import java.time.LocalDate;

/**
 * DTO for merchant profile update requests.
 * Email and National ID cannot be changed after registration.
 */
@Data
public class UpdateMerchantRequest {

    /** Merchant's updated full name */
    private String fullName;

    /** Merchant's updated phone number */
    private String phoneNumber;

    /** Merchant's updated date of birth */
    private LocalDate dateOfBirth;

    /** Merchant's updated business name */
    private String businessName;

    /** Merchant's updated bank name */
    private String bankName;

    /** Merchant's updated bank account number */
    private String bankAccountNumber;

    /** Merchant's updated bank account holder name */
    private String bankAccountHolderName;
}