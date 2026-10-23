package com.telecom.usageservice.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.telecom.usageservice.model.DailyUsage;

public interface DailyUsageRepository extends JpaRepository<DailyUsage, Long> {

    Optional<DailyUsage> findByMobileLineIdAndUsageDate(
            Long mobileLineId,
            LocalDate usageDate
    );

    List<DailyUsage> findByCustomerIdAndUsageDateBetween(
            Long customerId,
            LocalDate startDate,
            LocalDate endDate
    );

    List<DailyUsage> findByMobileLineIdAndUsageDateBetween(
            Long mobileLineId,
            LocalDate startDate,
            LocalDate endDate
    );

    List<DailyUsage> findBySubscriptionIdAndUsageDateBetween(
            Long subscriptionId,
            LocalDate startDate,
            LocalDate endDate
    );
}