package com.telecom.subscriptionservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.telecom.subscriptionservice.model.Subscription;
import com.telecom.subscriptionservice.model.SubscriptionStatus;

public interface SubscriptionRepository
        extends JpaRepository<Subscription, Long> {

    List<Subscription> findByCustomerId(Long customerId);

    List<Subscription> findByPhoneNumber(String phoneNumber);

    Optional<Subscription> findByPhoneNumberAndStatus(
            String phoneNumber,
            SubscriptionStatus status);

    boolean existsByPhoneNumberAndStatus(
            String phoneNumber,
            SubscriptionStatus status);
}