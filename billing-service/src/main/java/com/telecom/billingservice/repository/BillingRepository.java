package com.telecom.billingservice.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.telecom.billingservice.model.Billing;

public interface BillingRepository extends JpaRepository<Billing, Long> {

    Optional<Billing> findBySubscriptionIdAndBillingPeriodStartAndBillingPeriodEnd(
            Long subscriptionId,
            LocalDate billingPeriodStart,
            LocalDate billingPeriodEnd);

    List<Billing> findByCustomerIdOrderByBillingPeriodStartDesc(Long customerId);

    List<Billing> findByMobileLineIdOrderByBillingPeriodStartDesc(Long mobileLineId);

    List<Billing> findBySubscriptionIdOrderByBillingPeriodStartDesc(Long subscriptionId);
}