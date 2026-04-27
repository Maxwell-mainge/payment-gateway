package com.userservice.demo.admin.repository;

import com.userservice.demo.admin.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository for admin database operations.
 */
public interface AdminRepository extends JpaRepository<Admin, Long> {

    /** Find admin by email - used during login */
    Optional<Admin> findByEmail(String email);

    /** Check if admin account already exists */
    boolean existsByEmail(String email);
}