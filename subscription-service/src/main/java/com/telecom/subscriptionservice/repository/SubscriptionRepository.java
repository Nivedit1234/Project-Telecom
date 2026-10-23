package com.telecom.subscriptionservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.telecom.subscriptionservice.model.Subscription;
import com.telecom.subscriptionservice.model.SubscriptionStatus;

public interface SubscriptionRepository
        extends JpaRepository<Subscription, Long> {

    List<Subscription> findByMobileLineId(Long mobileLineId);

    Optional<Subscription> findByMobileLineIdAndStatus(
            Long mobileLineId,
            SubscriptionStatus status);

    boolean existsByMobileLineIdAndStatus(
            Long mobileLineId,
            SubscriptionStatus status);

    List<Subscription> findByStatus(SubscriptionStatus status);
}