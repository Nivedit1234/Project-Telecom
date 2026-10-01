package com.telecom.subscriptionservice.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.telecom.subscriptionservice.client.CatalogServiceClient;
import com.telecom.subscriptionservice.dto.SubscriptionRequest;
import com.telecom.subscriptionservice.dto.SubscriptionResponse;
import com.telecom.subscriptionservice.exception.DuplicateResourceException;
import com.telecom.subscriptionservice.exception.InvalidSubscriptionStateException;
import com.telecom.subscriptionservice.exception.ResourceNotFoundException;
import com.telecom.subscriptionservice.model.MobileLine;
import com.telecom.subscriptionservice.model.MobileLineStatus;
import com.telecom.subscriptionservice.model.Subscription;
import com.telecom.subscriptionservice.model.SubscriptionStatus;
import com.telecom.subscriptionservice.repository.MobileLineRepository;
import com.telecom.subscriptionservice.repository.SubscriptionRepository;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final MobileLineRepository mobileLineRepository;
    private final CatalogServiceClient catalogServiceClient;

    public SubscriptionService(
            SubscriptionRepository subscriptionRepository,
            MobileLineRepository mobileLineRepository,
            CatalogServiceClient catalogServiceClient) {

        this.subscriptionRepository = subscriptionRepository;
        this.mobileLineRepository = mobileLineRepository;
        this.catalogServiceClient = catalogServiceClient;
    }

    @Transactional
    public SubscriptionResponse createSubscription(
            SubscriptionRequest request) {

        MobileLine mobileLine = mobileLineRepository
                .findById(request.getMobileLineId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Mobile line not found with id: "
                                + request.getMobileLineId()));

        if (mobileLine.getStatus() != MobileLineStatus.ACTIVE) {
            throw new InvalidSubscriptionStateException(
                    "Subscription can only be created for an ACTIVE mobile line");
        }

        if (subscriptionRepository.existsByMobileLineIdAndStatus(
                request.getMobileLineId(),
                SubscriptionStatus.ACTIVE)) {

            throw new DuplicateResourceException(
                    "An active subscription already exists for mobile line id: "
                            + request.getMobileLineId());
        }

        catalogServiceClient.validatePlan(request.getPlanId());

        Subscription subscription = new Subscription();

        subscription.setMobileLineId(request.getMobileLineId());
        subscription.setPlanId(request.getPlanId());
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setStartDate(request.getStartDate());

        return mapToResponse(
                subscriptionRepository.save(subscription));
    }

    @Transactional(readOnly = true)
    public SubscriptionResponse getSubscriptionById(Long id) {

        Subscription subscription = findSubscription(id);

        return mapToResponse(subscription);
    }

    @Transactional(readOnly = true)
    public List<SubscriptionResponse> getSubscriptionsByMobileLine(
            Long mobileLineId) {

        return subscriptionRepository
                .findByMobileLineId(mobileLineId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SubscriptionResponse getActiveSubscription(
            Long mobileLineId) {

        Subscription subscription = subscriptionRepository
                .findByMobileLineIdAndStatus(
                        mobileLineId,
                        SubscriptionStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No active subscription found for mobile line id: "
                                + mobileLineId));

        return mapToResponse(subscription);
    }

    @Transactional(readOnly = true)
    public List<SubscriptionResponse> getSubscriptionsByStatus(
            SubscriptionStatus status) {

        return subscriptionRepository
                .findByStatus(status)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public SubscriptionResponse suspendSubscription(Long id) {

        Subscription subscription = findSubscription(id);

        if (subscription.getStatus() != SubscriptionStatus.ACTIVE) {
            throw new InvalidSubscriptionStateException(
                    "Only ACTIVE subscriptions can be suspended");
        }

        subscription.setStatus(SubscriptionStatus.SUSPENDED);

        return mapToResponse(
                subscriptionRepository.save(subscription));
    }

    @Transactional
    public SubscriptionResponse resumeSubscription(Long id) {

        Subscription subscription = findSubscription(id);

        if (subscription.getStatus() != SubscriptionStatus.SUSPENDED) {
            throw new InvalidSubscriptionStateException(
                    "Only SUSPENDED subscriptions can be resumed");
        }

        MobileLine mobileLine = mobileLineRepository
                .findById(subscription.getMobileLineId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Mobile line not found with id: "
                                + subscription.getMobileLineId()));

        if (mobileLine.getStatus() != MobileLineStatus.ACTIVE) {
            throw new InvalidSubscriptionStateException(
                    "Subscription cannot be resumed because the mobile line is not ACTIVE");
        }

        subscription.setStatus(SubscriptionStatus.ACTIVE);

        return mapToResponse(
                subscriptionRepository.save(subscription));
    }

    @Transactional
    public SubscriptionResponse cancelSubscription(Long id) {

        Subscription subscription = findSubscription(id);

        if (subscription.getStatus() != SubscriptionStatus.ACTIVE
                && subscription.getStatus() != SubscriptionStatus.SUSPENDED) {

            throw new InvalidSubscriptionStateException(
                    "Only ACTIVE or SUSPENDED subscriptions can be cancelled");
        }

        subscription.setStatus(SubscriptionStatus.CANCELLED);
        subscription.setEndDate(LocalDate.now());

        return mapToResponse(
                subscriptionRepository.save(subscription));
    }

    /**
     * Performs the actual plan change.
     *
     * Order Service calls this method after the order
     * validation checks have passed.
     */
    @Transactional
    public SubscriptionResponse changePlan(
            Long subscriptionId,
            Long newPlanId) {

        Subscription subscription = findSubscription(subscriptionId);

        if (subscription.getStatus() != SubscriptionStatus.ACTIVE) {
            throw new InvalidSubscriptionStateException(
                    "Only an ACTIVE subscription can change plans");
        }

        if (subscription.getPlanId().equals(newPlanId)) {
            throw new InvalidSubscriptionStateException(
                    "Requested plan is already assigned to the subscription");
        }

        /*
         * Catalog Service owns plan data.
         * Validate that the requested plan exists and is active.
         */
        catalogServiceClient.validatePlan(newPlanId);

        /*
         * Perform the actual plan change.
         */
        subscription.setPlanId(newPlanId);

        Subscription updatedSubscription =
                subscriptionRepository.save(subscription);

        return mapToResponse(updatedSubscription);
    }

    private Subscription findSubscription(Long id) {

        return subscriptionRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Subscription not found with id: " + id));
    }

    private SubscriptionResponse mapToResponse(
            Subscription subscription) {

        return new SubscriptionResponse(
                subscription.getId(),
                subscription.getMobileLineId(),
                subscription.getPlanId(),
                subscription.getStatus(),
                subscription.getStartDate(),
                subscription.getEndDate(),
                subscription.getCreatedAt(),
                subscription.getUpdatedAt());
    }
}