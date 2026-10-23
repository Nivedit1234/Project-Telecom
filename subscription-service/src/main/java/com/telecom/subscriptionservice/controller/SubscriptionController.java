package com.telecom.subscriptionservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.telecom.subscriptionservice.dto.SubscriptionRequest;
import com.telecom.subscriptionservice.dto.SubscriptionResponse;
import com.telecom.subscriptionservice.model.SubscriptionStatus;
import com.telecom.subscriptionservice.service.SubscriptionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(
            SubscriptionService subscriptionService) {

        this.subscriptionService = subscriptionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubscriptionResponse createSubscription(
            @Valid @RequestBody SubscriptionRequest request) {

        return subscriptionService.createSubscription(request);
    }

    @GetMapping("/{id}")
    public SubscriptionResponse getSubscriptionById(
            @PathVariable Long id) {

        return subscriptionService.getSubscriptionById(id);
    }

    @GetMapping("/mobile-line/{mobileLineId}")
    public List<SubscriptionResponse> getSubscriptionsByMobileLine(
            @PathVariable Long mobileLineId) {

        return subscriptionService.getSubscriptionsByMobileLine(
                mobileLineId);
    }

    @GetMapping("/mobile-line/{mobileLineId}/active")
    public SubscriptionResponse getActiveSubscription(
            @PathVariable Long mobileLineId) {

        return subscriptionService.getActiveSubscription(
                mobileLineId);
    }

    @GetMapping("/status")
    public List<SubscriptionResponse> getSubscriptionsByStatus(
            @RequestParam SubscriptionStatus status) {

        return subscriptionService.getSubscriptionsByStatus(status);
    }

    @PostMapping("/{id}/change-plan/{planId}")
    public SubscriptionResponse changePlan(
            @PathVariable Long id,
            @PathVariable Long planId) {

        return subscriptionService.changePlan(id, planId);
    }

    @PostMapping("/{id}/suspend")
    public SubscriptionResponse suspendSubscription(
            @PathVariable Long id) {

        return subscriptionService.suspendSubscription(id);
    }

    @PostMapping("/{id}/resume")
    public SubscriptionResponse resumeSubscription(
            @PathVariable Long id) {

        return subscriptionService.resumeSubscription(id);
    }

    @DeleteMapping("/{id}")
    public SubscriptionResponse cancelSubscription(
            @PathVariable Long id) {

        return subscriptionService.cancelSubscription(id);
    }
}