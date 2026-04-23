package com.userservice.demo.user.controller;

import com.userservice.demo.user.dto.MerchantRegisterRequest;
import com.userservice.demo.user.dto.RegisterRequest;
import com.userservice.demo.user.model.Customer;
import com.userservice.demo.user.model.Merchant;
import com.userservice.demo.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.userservice.demo.user.dto.AuthResponse;
import com.userservice.demo.user.dto.LoginRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register/customer")
    public ResponseEntity<Customer> registerCustomer(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.registerCustomer(request));
    }

    @PostMapping("/register/merchant")
    public ResponseEntity<Merchant> registerMerchant(@RequestBody MerchantRegisterRequest request) {
        return ResponseEntity.ok(userService.registerMerchant(request));
    }

    @GetMapping("/customers")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(userService.getAllCustomers());
    }

    @GetMapping("/customers/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getCustomerById(id));
    }

    @GetMapping("/merchants")
    public ResponseEntity<List<Merchant>> getAllMerchants() {
        return ResponseEntity.ok(userService.getAllMerchants());
    }

    @GetMapping("/merchants/{id}")
    public ResponseEntity<Merchant> getMerchantById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getMerchantById(id));
    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        return ResponseEntity.ok(userService.refreshToken(refreshToken));
    }
}