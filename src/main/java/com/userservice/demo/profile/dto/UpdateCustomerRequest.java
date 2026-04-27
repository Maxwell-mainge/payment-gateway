package com.userservice.demo.profile.dto;

import lombok.Data;
import java.time.LocalDate;

/**
 * DTO for customer profile update requests.
 * Email and National ID cannot be changed after registration.
 */
@Data
public class UpdateCustomerRequest {

    /** Customer's updated full name */
    private String fullName;

    /** Customer's updated phone number */
    private String phoneNumber;

    /** Customer's updated date of birth */
    private LocalDate dateOfBirth;
}