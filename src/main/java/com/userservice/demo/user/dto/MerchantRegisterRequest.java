package com.userservice.demo.user.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class MerchantRegisterRequest {
    private String fullName;
    private String email;
    private String phoneNumber;
    private String password;
    private String nationalId;
    private String kraPin;
    private LocalDate dateOfBirth;
    private boolean termsAccepted;
    private String businessName;
    private String businessRegistrationNumber;
    private String businessKraPin;
    private String businessType;
    private String bankName;
    private String bankAccountNumber;
    private String bankAccountHolderName;
}