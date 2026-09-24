package com.telecom.subscriptionservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.telecom.subscriptionservice.model.MobileLine;
import com.telecom.subscriptionservice.model.MobileLineStatus;

/**
 * Repository for MobileLine persistence.
 */
public interface MobileLineRepository
        extends JpaRepository<MobileLine, Long> {

    /**
     * Used to enforce globally unique phone numbers.
     */
    boolean existsByPhoneNumber(String phoneNumber);

    /**
     * Used to enforce globally unique SIM/ICCID numbers.
     */
    boolean existsBySimNumber(String simNumber);

    /**
     * Find a mobile line by its telecom number.
     */
    Optional<MobileLine> findByPhoneNumber(String phoneNumber);

    /**
     * Return all mobile lines owned by a customer.
     */
    List<MobileLine> findByCustomerId(Long customerId);

    /**
     * Useful for finding a customer's lines by status.
     */
    List<MobileLine> findByCustomerIdAndStatus(
            Long customerId,
            MobileLineStatus status);

    /**
     * Useful for checking whether a customer already has
     * a particular phone number.
     */
    Optional<MobileLine> findByPhoneNumberAndCustomerId(
            String phoneNumber,
            Long customerId);
}