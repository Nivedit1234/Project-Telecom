package com.telecom.invoiceservice.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BillingGeneratedEvent(
        Long billingId,
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
        BigDecimal totalAmount
) {
}