package com.userservice.demo.profile.service;

import com.userservice.demo.profile.dto.UpdateCustomerRequest;
import com.userservice.demo.profile.dto.UpdateMerchantRequest;
import com.userservice.demo.user.model.Customer;
import com.userservice.demo.user.model.Merchant;
import com.userservice.demo.user.repository.CustomerRepository;
import com.userservice.demo.user.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service for profile update operations.
 * Handles updating customer and merchant profile details.
 * Email and National ID cannot be updated.
 */
@Service
@RequiredArgsConstructor
public class ProfileService {

    private final CustomerRepository customerRepository;
    private final MerchantRepository merchantRepository;

    /**
     * Updates a customer's profile details.
     * Only fullName, phoneNumber and dateOfBirth can be updated.
     *
     * @param email   the email of the logged in customer from JWT
     * @param request the updated profile details
     * @return the updated Customer object
     */
    public Customer updateCustomerProfile(String email, UpdateCustomerRequest request) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Check if new phone number is already taken by another customer
        if (request.getPhoneNumber() != null &&
                !request.getPhoneNumber().equals(customer.getPhoneNumber()) &&
                customerRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("Phone number already in use");
        }

        // Update allowed fields only
        if (request.getFullName() != null) {
            customer.setFullName(request.getFullName());
        }
        if (request.getPhoneNumber() != null) {
            customer.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getDateOfBirth() != null) {
            customer.setDateOfBirth(request.getDateOfBirth());
        }

        return customerRepository.save(customer);
    }

    /**
     * Updates a merchant's profile details.
     * Only fullName, phoneNumber, dateOfBirth and business details can be updated.
     *
     * @param email   the email of the logged in merchant from JWT
     * @param request the updated profile details
     * @return the updated Merchant object
     */
    public Merchant updateMerchantProfile(String email, UpdateMerchantRequest request) {
        Merchant merchant = merchantRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Merchant not found"));

        // Check if new phone number is already taken by another merchant
        if (request.getPhoneNumber() != null &&
                !request.getPhoneNumber().equals(merchant.getPhoneNumber()) &&
                merchantRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("Phone number already in use");
        }

        // Update allowed fields only
        if (request.getFullName() != null) {
            merchant.setFullName(request.getFullName());
        }
        if (request.getPhoneNumber() != null) {
            merchant.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getDateOfBirth() != null) {
            merchant.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getBusinessName() != null) {
            merchant.setBusinessName(request.getBusinessName());
        }
        if (request.getBankName() != null) {
            merchant.setBankName(request.getBankName());
        }
        if (request.getBankAccountNumber() != null) {
            merchant.setBankAccountNumber(request.getBankAccountNumber());
        }
        if (request.getBankAccountHolderName() != null) {
            merchant.setBankAccountHolderName(request.getBankAccountHolderName());
        }

        return merchantRepository.save(merchant);
    }
}