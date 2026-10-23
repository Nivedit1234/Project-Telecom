package com.telecom.billingservice.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.telecom.billingservice.model.BillingStatus;

public record BillingResponse(
        Long id,
        Long customerId,
        Long mobileLineId,
        Long subscriptionId,
        Long planId,
        LocalDate billingPeriodStart,
        LocalDate billingPeriodEnd,
        BigDecimal callMinutes,
        BigDecimal dataMb,
        Long smsCount,
        BigDecimal callCharges,
        BigDecimal dataCharges,
        BigDecimal smsCharges,
        BigDecimal planCharges,
        BigDecimal totalAmount,
        BillingStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}