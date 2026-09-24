package com.telecom.userservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.telecom.userservice.model.Customer;

public interface CustomerRepository
        extends JpaRepository<Customer, Long> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(
            String email,
            Long id);

    Optional<Customer> findByUserEmail(String email);
}