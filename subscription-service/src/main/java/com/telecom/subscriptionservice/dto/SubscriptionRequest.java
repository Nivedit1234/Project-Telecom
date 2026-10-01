package com.telecom.subscriptionservice.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionRequest {

    @NotNull
    private Long mobileLineId;

    @NotNull
    private Long planId;

    @NotNull
    @FutureOrPresent(message = "Start date cannot be in the past")
    private LocalDate startDate;
}