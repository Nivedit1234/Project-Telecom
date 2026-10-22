package com.telecom.usageservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.telecom.usageservice.model.UsageType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record UsageEvent(

        @NotNull
        Long customerId,

        @NotNull
        Long mobileLineId,

        @NotNull
        Long subscriptionId,

        @NotNull
        Long planId,

        @NotNull
        UsageType usageType,

        @NotNull
        @DecimalMin(value = "0.0", inclusive = true)
        BigDecimal quantity,

        @NotNull
        LocalDateTime eventTime
) {
}