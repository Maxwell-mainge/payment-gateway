package com.userservice.demo.user.service;

import com.userservice.demo.user.dto.MerchantRegisterRequest;
import com.userservice.demo.user.dto.RegisterRequest;
import com.userservice.demo.user.model.Customer;
import com.userservice.demo.user.model.Merchant;
import com.userservice.demo.user.repository.CustomerRepository;
import com.userservice.demo.user.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import com.userservice.demo.user.dto.AuthResponse;
import com.userservice.demo.user.dto.LoginRequest;
import com.userservice.demo.security.JwtUtil;
import java.util.Optional;
import com.userservice.demo.otp.service.OtpService;
import com.userservice.demo.otp.model.OtpRecord;
import com.userservice.demo.admin.model.Admin;
import com.userservice.demo.admin.repository.AdminRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final CustomerRepository customerRepository;
    private final MerchantRepository merchantRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final OtpService otpService;
    private final AdminRepository adminRepository;

    public Customer registerCustomer(RegisterRequest request) {
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        if (customerRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("Phone number already exists");
        }
        if (customerRepository.existsByNationalId(request.getNationalId())) {
            throw new RuntimeException("National ID already exists");
        }
        if (customerRepository.existsByKraPin(request.getKraPin())) {
            throw new RuntimeException("KRA PIN already exists");
        }
        if (!request.isTermsAccepted()) {
            throw new RuntimeException("You must accept the terms and conditions");
        }

        Customer customer = new Customer();
        customer.setFullName(request.getFullName());
        customer.setEmail(request.getEmail());
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setPassword(passwordEncoder.encode(request.getPassword()));
        customer.setNationalId(request.getNationalId());
        customer.setKraPin(request.getKraPin());
        customer.setDateOfBirth(request.getDateOfBirth());
        customer.setAccountStatus(Customer.AccountStatus.PENDING);
        customer.setTermsAccepted(true);
        customer.setTermsAcceptedAt(LocalDateTime.now());

        Customer savedCustomer = customerRepository.save(customer);

// Send email OTP
        otpService.generateOtp(savedCustomer.getEmail(), OtpRecord.OtpType.EMAIL);

// Send phone OTP
        otpService.generateOtp(savedCustomer.getPhoneNumber(), OtpRecord.OtpType.PHONE);

        return savedCustomer;
    }

    public Merchant registerMerchant(MerchantRegisterRequest request) {
        if (merchantRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        if (merchantRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("Phone number already exists");
        }
        if (merchantRepository.existsByNationalId(request.getNationalId())) {
            throw new RuntimeException("National ID already exists");
        }
        if (merchantRepository.existsByKraPin(request.getKraPin())) {
            throw new RuntimeException("KRA PIN already exists");
        }
        if (merchantRepository.existsByBusinessRegistrationNumber(request.getBusinessRegistrationNumber())) {
            throw new RuntimeException("Business registration number already exists");
        }
        if (merchantRepository.existsByBusinessKraPin(request.getBusinessKraPin())) {
            throw new RuntimeException("Business KRA PIN already exists");
        }
        if (!request.isTermsAccepted()) {
            throw new RuntimeException("You must accept the terms and conditions");
        }

        Merchant merchant = new Merchant();
        merchant.setFullName(request.getFullName());
        merchant.setEmail(request.getEmail());
        merchant.setPhoneNumber(request.getPhoneNumber());
        merchant.setPassword(passwordEncoder.encode(request.getPassword()));
        merchant.setNationalId(request.getNationalId());
        merchant.setKraPin(request.getKraPin());
        merchant.setDateOfBirth(request.getDateOfBirth());
        merchant.setBusinessName(request.getBusinessName());
        merchant.setBusinessRegistrationNumber(request.getBusinessRegistrationNumber());
        merchant.setBusinessKraPin(request.getBusinessKraPin());
        merchant.setBusinessType(request.getBusinessType());
        merchant.setBankName(request.getBankName());
        merchant.setBankAccountNumber(request.getBankAccountNumber());
        merchant.setBankAccountHolderName(request.getBankAccountHolderName());
        merchant.setAccountStatus(Merchant.AccountStatus.PENDING);
        merchant.setVerificationStatus(Merchant.VerificationStatus.PENDING);
        merchant.setTermsAccepted(true);
        merchant.setTermsAcceptedAt(LocalDateTime.now());

        Merchant savedMerchant = merchantRepository.save(merchant);

// Send email OTP
        otpService.generateOtp(savedMerchant.getEmail(), OtpRecord.OtpType.EMAIL);

// Send phone OTP
        otpService.generateOtp(savedMerchant.getPhoneNumber(), OtpRecord.OtpType.PHONE);

        return savedMerchant;
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    public List<Merchant> getAllMerchants() {
        return merchantRepository.findAll();
    }

    public Merchant getMerchantById(Long id) {
        return merchantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Merchant not found"));
    }
    public AuthResponse login(LoginRequest request) {
        // Check customers first
        Optional<Customer> customer = customerRepository.findByEmail(request.getEmail());
        if (customer.isPresent()) {
            if (!passwordEncoder.matches(request.getPassword(), customer.get().getPassword())) {
                throw new RuntimeException("Invalid password");
            }
            if (customer.get().getAccountStatus() == Customer.AccountStatus.SUSPENDED) {
                throw new RuntimeException("Account is suspended");
            }
            String accessToken = jwtUtil.generateAccessToken(customer.get().getEmail(), "CUSTOMER");
            String refreshToken = jwtUtil.generateRefreshToken(customer.get().getEmail());
            return new AuthResponse(accessToken, refreshToken, "CUSTOMER", customer.get().getFullName());
        }

        // Check merchants
        Optional<Merchant> merchant = merchantRepository.findByEmail(request.getEmail());
        if (merchant.isPresent()) {
            if (!passwordEncoder.matches(request.getPassword(), merchant.get().getPassword())) {
                throw new RuntimeException("Invalid password");
            }
            if (merchant.get().getAccountStatus() == Merchant.AccountStatus.SUSPENDED) {
                throw new RuntimeException("Account is suspended");
            }
            String accessToken = jwtUtil.generateAccessToken(merchant.get().getEmail(), "MERCHANT");
            String refreshToken = jwtUtil.generateRefreshToken(merchant.get().getEmail());
            return new AuthResponse(accessToken, refreshToken, "MERCHANT", merchant.get().getFullName());
        }
        // Check admin
        Optional<Admin> admin = adminRepository.findByEmail(request.getEmail());
        if (admin.isPresent()) {
            if (!passwordEncoder.matches(request.getPassword(), admin.get().getPassword())) {
                throw new RuntimeException("Invalid password");
            }
            String accessToken = jwtUtil.generateAccessToken(admin.get().getEmail(), "ADMIN");
            String refreshToken = jwtUtil.generateRefreshToken(admin.get().getEmail());
            return new AuthResponse(accessToken, refreshToken, "ADMIN", admin.get().getFullName());
        }

        throw new RuntimeException("User not found");
    }
    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtUtil.isTokenValid(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }
        if (!jwtUtil.isRefreshToken(refreshToken)) {
            throw new RuntimeException("Not a refresh token");
        }

        String email = jwtUtil.extractEmail(refreshToken);

        // Check customers first
        Optional<Customer> customer = customerRepository.findByEmail(email);
        if (customer.isPresent()) {
            String newAccessToken = jwtUtil.generateAccessToken(email, "CUSTOMER");
            String newRefreshToken = jwtUtil.generateRefreshToken(email);
            return new AuthResponse(newAccessToken, newRefreshToken, "CUSTOMER", customer.get().getFullName());
        }

        // Check merchants
        Optional<Merchant> merchant = merchantRepository.findByEmail(email);
        if (merchant.isPresent()) {
            String newAccessToken = jwtUtil.generateAccessToken(email, "MERCHANT");
            String newRefreshToken = jwtUtil.generateRefreshToken(email);
            return new AuthResponse(newAccessToken, newRefreshToken, "MERCHANT", merchant.get().getFullName());
        }

        throw new RuntimeException("User not found");
    }
}