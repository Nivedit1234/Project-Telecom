package com.telecom.billingservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.telecom.billingservice.dto.BillingRequest;
import com.telecom.billingservice.dto.BillingResponse;
import com.telecom.billingservice.service.BillingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/billing")
public class BillingController {

    private final BillingService billingService;

    public BillingController(BillingService billingService) {
        this.billingService = billingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BillingResponse generateBilling(
            @Valid @RequestBody BillingRequest request) {

        return billingService.generateBilling(request);
    }

    @GetMapping("/{id}")
    public BillingResponse getBillingById(
            @PathVariable Long id) {

        return billingService.getBillingById(id);
    }

    @GetMapping("/customer/{customerId}")
    public List<BillingResponse> getBillingByCustomer(
            @PathVariable Long customerId) {

        return billingService.getBillingByCustomer(customerId);
    }

    @GetMapping("/mobile-line/{mobileLineId}")
    public List<BillingResponse> getBillingByMobileLine(
            @PathVariable Long mobileLineId) {

        return billingService.getBillingByMobileLine(mobileLineId);
    }

    @GetMapping("/subscription/{subscriptionId}")
    public List<BillingResponse> getBillingBySubscription(
            @PathVariable Long subscriptionId) {

        return billingService.getBillingBySubscription(subscriptionId);
    }
}