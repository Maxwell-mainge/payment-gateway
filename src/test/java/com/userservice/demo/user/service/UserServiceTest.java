package com.userservice.demo.user.service;

import com.userservice.demo.user.dto.MerchantRegisterRequest;
import com.userservice.demo.user.dto.RegisterRequest;
import com.userservice.demo.user.model.Customer;
import com.userservice.demo.user.model.Merchant;
import com.userservice.demo.user.repository.CustomerRepository;
import com.userservice.demo.user.repository.MerchantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import com.userservice.demo.user.dto.AuthResponse;
import com.userservice.demo.user.dto.LoginRequest;
import com.userservice.demo.security.JwtUtil;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private MerchantRepository merchantRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldRegisterCustomerSuccessfully() {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("Maxwell Mainge");
        request.setEmail("max@gmail.com");
        request.setPhoneNumber("0712345678");
        request.setPassword("Password123");
        request.setNationalId("12345678");
        request.setKraPin("A123456789B");
        request.setTermsAccepted(true);

        when(customerRepository.existsByEmail(any())).thenReturn(false);
        when(customerRepository.existsByPhoneNumber(any())).thenReturn(false);
        when(customerRepository.existsByNationalId(any())).thenReturn(false);
        when(customerRepository.existsByKraPin(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(customerRepository.save(any(Customer.class))).thenAnswer(i -> i.getArgument(0));

        Customer result = userService.registerCustomer(request);

        assertNotNull(result);
        assertEquals("Maxwell Mainge", result.getFullName());
        assertEquals(Customer.AccountStatus.PENDING, result.getAccountStatus());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void shouldThrowExceptionWhenCustomerEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("max@gmail.com");

        when(customerRepository.existsByEmail("max@gmail.com")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> {
            userService.registerCustomer(request);
        });
    }

    @Test
    void shouldRegisterMerchantSuccessfully() {
        MerchantRegisterRequest request = new MerchantRegisterRequest();
        request.setFullName("John Doe");
        request.setEmail("john@business.com");
        request.setPhoneNumber("0723456789");
        request.setPassword("Password123");
        request.setNationalId("87654321");
        request.setKraPin("B987654321A");
        request.setTermsAccepted(true);
        request.setBusinessName("John's Shop");
        request.setBusinessRegistrationNumber("BUS123456");
        request.setBusinessKraPin("C111222333D");
        request.setBusinessType("Retail");
        request.setBankName("Equity Bank");
        request.setBankAccountNumber("1234567890");
        request.setBankAccountHolderName("John Doe");

        when(merchantRepository.existsByEmail(any())).thenReturn(false);
        when(merchantRepository.existsByPhoneNumber(any())).thenReturn(false);
        when(merchantRepository.existsByNationalId(any())).thenReturn(false);
        when(merchantRepository.existsByKraPin(any())).thenReturn(false);
        when(merchantRepository.existsByBusinessRegistrationNumber(any())).thenReturn(false);
        when(merchantRepository.existsByBusinessKraPin(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(merchantRepository.save(any(Merchant.class))).thenAnswer(i -> i.getArgument(0));

        Merchant result = userService.registerMerchant(request);

        assertNotNull(result);
        assertEquals("John's Shop", result.getBusinessName());
        assertEquals(Merchant.VerificationStatus.PENDING, result.getVerificationStatus());
        verify(merchantRepository, times(1)).save(any(Merchant.class));
    }
    @Test
    void shouldLoginCustomerSuccessfully() {
        LoginRequest request = new LoginRequest();
        request.setEmail("max@gmail.com");
        request.setPassword("Password123");

        Customer customer = new Customer();
        customer.setEmail("max@gmail.com");
        customer.setPassword("encodedPassword");
        customer.setFullName("Maxwell Mainge");
        customer.setAccountStatus(Customer.AccountStatus.ACTIVE);

        when(customerRepository.findByEmail("max@gmail.com"))
                .thenReturn(Optional.of(customer));
        when(passwordEncoder.matches("Password123", "encodedPassword"))
                .thenReturn(true);
        when(jwtUtil.generateToken("max@gmail.com", "CUSTOMER"))
                .thenReturn("mockToken");

        AuthResponse response = userService.login(request);

        assertNotNull(response);
        assertEquals("mockToken", response.getToken());
        assertEquals("CUSTOMER", response.getRole());
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsWrong() {
        LoginRequest request = new LoginRequest();
        request.setEmail("max@gmail.com");
        request.setPassword("wrongpassword");

        Customer customer = new Customer();
        customer.setEmail("max@gmail.com");
        customer.setPassword("encodedPassword");
        customer.setAccountStatus(Customer.AccountStatus.ACTIVE);

        when(customerRepository.findByEmail("max@gmail.com"))
                .thenReturn(Optional.of(customer));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword"))
                .thenReturn(false);

        assertThrows(RuntimeException.class, () -> {
            userService.login(request);
        });
    }
}