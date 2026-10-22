package com.telecom.usageservice.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.telecom.usageservice.dto.DailyUsageResponse;
import com.telecom.usageservice.model.DailyUsage;
import com.telecom.usageservice.repository.DailyUsageRepository;

@RestController
@RequestMapping("/api/usage")
public class UsageController {

    private final DailyUsageRepository dailyUsageRepository;

    public UsageController(DailyUsageRepository dailyUsageRepository) {
        this.dailyUsageRepository = dailyUsageRepository;
    }

    @GetMapping("/mobile-line/{mobileLineId}")
    public ResponseEntity<List<DailyUsageResponse>> getUsageByMobileLine(
            @PathVariable Long mobileLineId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {

        List<DailyUsageResponse> response = dailyUsageRepository
                .findByMobileLineIdAndUsageDateBetween(mobileLineId, startDate, endDate)
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<DailyUsageResponse>> getUsageByCustomer(
            @PathVariable Long customerId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {

        List<DailyUsageResponse> response = dailyUsageRepository
                .findByCustomerIdAndUsageDateBetween(customerId, startDate, endDate)
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/subscription/{subscriptionId}")
    public ResponseEntity<List<DailyUsageResponse>> getUsageBySubscription(
            @PathVariable Long subscriptionId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {

        List<DailyUsageResponse> response = dailyUsageRepository
                .findBySubscriptionIdAndUsageDateBetween(subscriptionId, startDate, endDate)
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/mobile-line/{mobileLineId}/date/{usageDate}")
    public ResponseEntity<DailyUsageResponse> getUsageForDate(
            @PathVariable Long mobileLineId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate usageDate) {

        return dailyUsageRepository
                .findByMobileLineIdAndUsageDate(mobileLineId, usageDate)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private DailyUsageResponse toResponse(DailyUsage usage) {
        return new DailyUsageResponse(
                usage.getId(),
                usage.getCustomerId(),
                usage.getMobileLineId(),
                usage.getSubscriptionId(),
                usage.getPlanId(),
                usage.getUsageDate(),
                usage.getCallMinutes(),
                usage.getDataMb(),
                usage.getSmsCount(),
                usage.getCreatedAt(),
                usage.getUpdatedAt()
        );
    }
}