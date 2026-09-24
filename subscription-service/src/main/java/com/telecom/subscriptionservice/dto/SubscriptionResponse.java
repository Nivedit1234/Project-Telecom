package com.telecom.subscriptionservice.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.telecom.subscriptionservice.model.SubscriptionStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionResponse {

    private Long id;

    private Long customerId;

    private String phoneNumber;

    private Long planId;

    private SubscriptionStatus status;

    private LocalDate startDate;

    private LocalDate endDate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}