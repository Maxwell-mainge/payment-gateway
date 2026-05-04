package com.userservice.demo.admin.service;

import com.userservice.demo.user.model.Customer;
import com.userservice.demo.user.model.Merchant;
import com.userservice.demo.user.repository.CustomerRepository;
import com.userservice.demo.user.repository.MerchantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private MerchantRepository merchantRepository;

    @InjectMocks
    private AdminService adminService;

    @Test
    void shouldSuspendCustomerSuccessfully() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setAccountStatus(Customer.AccountStatus.ACTIVE);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        adminService.suspendCustomer(1L);

        assertEquals(Customer.AccountStatus.SUSPENDED, customer.getAccountStatus());
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    void shouldUnlockCustomerSuccessfully() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setAccountStatus(Customer.AccountStatus.SUSPENDED);
        customer.setFailedLoginAttempts(5);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        adminService.unlockCustomer(1L);

        assertEquals(Customer.AccountStatus.ACTIVE, customer.getAccountStatus());
        assertEquals(0, customer.getFailedLoginAttempts());
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    void shouldThrowExceptionWhenCustomerNotFound() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> adminService.suspendCustomer(99L));
    }

    @Test
    void shouldSuspendMerchantSuccessfully() {
        Merchant merchant = new Merchant();
        merchant.setId(1L);
        merchant.setAccountStatus(Merchant.AccountStatus.ACTIVE);

        when(merchantRepository.findById(1L)).thenReturn(Optional.of(merchant));
        when(merchantRepository.save(any(Merchant.class))).thenReturn(merchant);

        adminService.suspendMerchant(1L);

        assertEquals(Merchant.AccountStatus.SUSPENDED, merchant.getAccountStatus());
        verify(merchantRepository, times(1)).save(merchant);
    }
}