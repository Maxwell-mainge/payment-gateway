package com.userservice.demo.admin.service;

import com.userservice.demo.user.model.Customer;
import com.userservice.demo.user.model.Merchant;
import com.userservice.demo.user.repository.CustomerRepository;
import com.userservice.demo.user.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for admin operations.
 * Handles customer and merchant management including
 * suspension, unlocking and deletion.
 */
@Service
@RequiredArgsConstructor
public class AdminService {

    private final CustomerRepository customerRepository;
    private final MerchantRepository merchantRepository;

    /**
     * Returns all customers in the system.
     */
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    /**
     * Returns a single customer by ID.
     */
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    /**
     * Suspends a customer account.
     */
    public void suspendCustomer(Long id) {
        Customer customer = getCustomerById(id);
        customer.setAccountStatus(Customer.AccountStatus.SUSPENDED);
        customerRepository.save(customer);
    }

    /**
     * Unsuspends a customer account.
     */
    public void unsuspendCustomer(Long id) {
        Customer customer = getCustomerById(id);
        customer.setAccountStatus(Customer.AccountStatus.ACTIVE);
        customerRepository.save(customer);
    }

    /**
     * Unlocks a locked customer account.
     * Resets failed login attempts and sets status back to ACTIVE.
     */
    public void unlockCustomer(Long id) {
        Customer customer = getCustomerById(id);
        customer.setFailedLoginAttempts(0);
        customer.setAccountStatus(Customer.AccountStatus.ACTIVE);
        customerRepository.save(customer);
    }

    /**
     * Deletes a customer account permanently.
     */
    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }

    /**
     * Returns all merchants in the system.
     */
    public List<Merchant> getAllMerchants() {
        return merchantRepository.findAll();
    }

    /**
     * Returns a single merchant by ID.
     */
    public Merchant getMerchantById(Long id) {
        return merchantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Merchant not found"));
    }

    /**
     * Suspends a merchant account.
     */
    public void suspendMerchant(Long id) {
        Merchant merchant = getMerchantById(id);
        merchant.setAccountStatus(Merchant.AccountStatus.SUSPENDED);
        merchantRepository.save(merchant);
    }

    /**
     * Unsuspends a merchant account.
     */
    public void unsuspendMerchant(Long id) {
        Merchant merchant = getMerchantById(id);
        merchant.setAccountStatus(Merchant.AccountStatus.ACTIVE);
        merchantRepository.save(merchant);
    }

    /**
     * Unlocks a locked merchant account.
     * Resets failed login attempts and sets status back to ACTIVE.
     */
    public void unlockMerchant(Long id) {
        Merchant merchant = getMerchantById(id);
        merchant.setFailedLoginAttempts(0);
        merchant.setAccountStatus(Merchant.AccountStatus.ACTIVE);
        merchantRepository.save(merchant);
    }

    /**
     * Deletes a merchant account permanently.
     */
    public void deleteMerchant(Long id) {
        merchantRepository.deleteById(id);
    }
}