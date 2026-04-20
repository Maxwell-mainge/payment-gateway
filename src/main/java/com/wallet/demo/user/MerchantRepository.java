package com.wallet.demo.user;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface MerchantRepository extends JpaRepository<Merchant, Long> {
    Optional<Merchant> findByUser(User user);
    Optional<Merchant> findByBusinessRegistrationNumber(String registrationNumber);
    List<Merchant> findByVerificationStatus(Merchant.VerificationStatus status);
    boolean existsByBusinessRegistrationNumber(String registrationNumber);
    boolean existsByBusinessKraPin(String businessKraPin);
}