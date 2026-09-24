package com.telecom.subscriptionservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.telecom.subscriptionservice.dto.SubscriptionRequest;
import com.telecom.subscriptionservice.dto.SubscriptionResponse;
import com.telecom.subscriptionservice.service.SubscriptionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(
            SubscriptionService subscriptionService) {

        this.subscriptionService =
                subscriptionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubscriptionResponse createSubscription(
            @Valid @RequestBody SubscriptionRequest request) {

        return subscriptionService.createSubscription(
                request);
    }

    @GetMapping("/{id}")
    public SubscriptionResponse getSubscriptionById(
            @PathVariable Long id) {

        return subscriptionService
                .getSubscriptionById(id);
    }

    @GetMapping("/customer/{customerId}")
    public List<SubscriptionResponse>
            getSubscriptionsByCustomerId(
                    @PathVariable Long customerId) {

        return subscriptionService
                .getSubscriptionsByCustomerId(
                        customerId);
    }

    @GetMapping("/phone/{phoneNumber}")
    public List<SubscriptionResponse>
            getSubscriptionHistoryByPhoneNumber(
                    @PathVariable String phoneNumber) {

        return subscriptionService
                .getSubscriptionHistoryByPhoneNumber(
                        phoneNumber);
    }

    @GetMapping("/phone/{phoneNumber}/active")
    public SubscriptionResponse
            getActiveSubscriptionByPhoneNumber(
                    @PathVariable String phoneNumber) {

        return subscriptionService
                .getActiveSubscriptionByPhoneNumber(
                        phoneNumber);
    }

    @PostMapping("/{id}/suspend")
    public SubscriptionResponse suspendSubscription(
            @PathVariable Long id) {

        return subscriptionService
                .suspendSubscription(id);
    }

    @PostMapping("/{id}/resume")
    public SubscriptionResponse resumeSubscription(
            @PathVariable Long id) {

        return subscriptionService
                .resumeSubscription(id);
    }

    @DeleteMapping("/{id}")
    public SubscriptionResponse cancelSubscription(
            @PathVariable Long id) {

        return subscriptionService
                .cancelSubscription(id);
    }
}