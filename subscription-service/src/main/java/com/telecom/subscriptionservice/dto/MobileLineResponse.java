package com.telecom.subscriptionservice.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.telecom.subscriptionservice.model.MobileLineStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * API response representing a customer's mobile line.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MobileLineResponse {

    private Long id;

    private Long customerId;

    private String phoneNumber;

    private String simNumber;

    private MobileLineStatus status;

    private LocalDate activationDate;

    private LocalDate deactivationDate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}