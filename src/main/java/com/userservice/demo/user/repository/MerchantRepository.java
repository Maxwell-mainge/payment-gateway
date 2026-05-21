package com.userservice.demo.user.repository;

import com.userservice.demo.user.model.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MerchantRepository extends JpaRepository<Merchant, Long> {
    Optional<Merchant> findByEmail(String email);
    Optional<Merchant> findByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByNationalId(String nationalId);
    boolean existsByKraPin(String kraPin);
    boolean existsByBusinessRegistrationNumber(String registrationNumber);
    boolean existsByBusinessKraPin(String businessKraPin);
}