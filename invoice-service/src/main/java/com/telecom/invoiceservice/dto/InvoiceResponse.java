package com.telecom.invoiceservice.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.telecom.invoiceservice.model.InvoiceStatus;

public record InvoiceResponse(

        Long id,

        String invoiceNumber,

        Long billingId,

        Long customerId,

        Long mobileLineId,

        Long subscriptionId,

        Long planId,

        LocalDate billingPeriodStart,

        LocalDate billingPeriodEnd,

        LocalDate invoiceDate,

        BigDecimal callMinutes,

        BigDecimal dataMb,

        Long smsCount,

        BigDecimal callCharges,

        BigDecimal dataCharges,

        BigDecimal smsCharges,

        BigDecimal planCharges,

        BigDecimal totalAmount,

        String s3Key,

        InvoiceStatus status,

        LocalDateTime createdAt,

        LocalDateTime updatedAt
) {
}