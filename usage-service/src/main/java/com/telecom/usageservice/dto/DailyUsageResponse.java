package com.telecom.usageservice.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record DailyUsageResponse(

        Long id,

        Long customerId,

        Long mobileLineId,

        Long subscriptionId,

        Long planId,

        LocalDate usageDate,

        BigDecimal callMinutes,

        BigDecimal dataMb,

        Long smsCount,

        LocalDateTime createdAt,

        LocalDateTime updatedAt
) {
}