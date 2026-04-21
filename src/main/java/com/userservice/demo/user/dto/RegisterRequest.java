package com.userservice.demo.user.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class RegisterRequest {
    private String fullName;
    private String email;
    private String phoneNumber;
    private String password;
    private String nationalId;
    private String kraPin;
    private LocalDate dateOfBirth;
    private boolean termsAccepted;
}