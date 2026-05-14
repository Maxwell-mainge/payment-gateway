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

@Service
@RequiredArgsConstructor
public class UserService {

    private final CustomerRepository customerRepository;
    private final MerchantRepository merchantRepository;
    private final PasswordEncoder passwordEncoder;

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

        return customerRepository.save(customer);
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

        return merchantRepository.save(merchant);
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
}