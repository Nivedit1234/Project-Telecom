package com.telecom.billingservice.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BillingRequest(

        @NotNull
        @Positive
        Long customerId,

        @NotNull
        @Positive
        Long mobileLineId,

        @NotNull
        @Positive
        Long subscriptionId,

        @NotNull
        @Positive
        Long planId,

        @NotNull
        LocalDate billingPeriodStart,

        @NotNull
        LocalDate billingPeriodEnd
) {
}