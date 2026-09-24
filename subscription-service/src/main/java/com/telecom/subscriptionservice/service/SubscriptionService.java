package com.telecom.subscriptionservice.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.telecom.subscriptionservice.client.CatalogServiceClient;
import com.telecom.subscriptionservice.client.UserServiceClient;
import com.telecom.subscriptionservice.dto.SubscriptionRequest;
import com.telecom.subscriptionservice.dto.SubscriptionResponse;
import com.telecom.subscriptionservice.exception.DuplicateResourceException;
import com.telecom.subscriptionservice.exception.InvalidSubscriptionStateException;
import com.telecom.subscriptionservice.exception.ResourceNotFoundException;
import com.telecom.subscriptionservice.model.Subscription;
import com.telecom.subscriptionservice.model.SubscriptionStatus;
import com.telecom.subscriptionservice.repository.SubscriptionRepository;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    /*
     * Client used to communicate with User Service.
     */
    private final UserServiceClient userServiceClient;

    /*
     * Client used to communicate with Catalog Service.
     */
    private final CatalogServiceClient catalogServiceClient;

    public SubscriptionService(
            SubscriptionRepository subscriptionRepository,
            UserServiceClient userServiceClient,
            CatalogServiceClient catalogServiceClient) {

        this.subscriptionRepository =
                subscriptionRepository;

        this.userServiceClient =
                userServiceClient;

        this.catalogServiceClient =
                catalogServiceClient;
    }

    /*
     * ========================================================
     * CREATE SUBSCRIPTION
     * ========================================================
     *
     * Business flow:
     *
     * 1. Validate customer through User Service.
     * 2. Validate plan through Catalog Service.
     * 3. Check whether phone already has an ACTIVE
     *    subscription.
     * 4. Create and save the subscription.
     */
    @Transactional
    public SubscriptionResponse createSubscription(
            SubscriptionRequest request) {

        /*
         * ----------------------------------------------------
         * STEP 1
         * ----------------------------------------------------
         *
         * Ask User Service:
         *
         * "Does this customer exist?"
         *
         * Customer data belongs to User Service.
         */
        userServiceClient.validateCustomer(
                request.getCustomerId()
        );


        /*
         * ----------------------------------------------------
         * STEP 2
         * ----------------------------------------------------
         *
         * Ask Catalog Service:
         *
         * "Does this plan exist?"
         *
         * Plan data belongs to Catalog Service.
         */
        catalogServiceClient.validatePlan(
                request.getPlanId()
        );


        /*
         * ----------------------------------------------------
         * STEP 3
         * ----------------------------------------------------
         *
         * Business rule:
         *
         * One phone number can have only ONE
         * ACTIVE subscription at a time.
         *
         * Historical subscriptions are still allowed.
         */
        boolean activeSubscriptionExists =
                subscriptionRepository
                        .existsByPhoneNumberAndStatus(
                                request.getPhoneNumber(),
                                SubscriptionStatus.ACTIVE
                        );

        if (activeSubscriptionExists) {

            throw new DuplicateResourceException(
                    "Phone number already has an active subscription: "
                            + request.getPhoneNumber()
            );
        }


        /*
         * ----------------------------------------------------
         * STEP 4
         * ----------------------------------------------------
         *
         * All validations passed.
         *
         * Now create the subscription.
         */
        Subscription subscription =
                new Subscription();

        subscription.setCustomerId(
                request.getCustomerId()
        );

        subscription.setPhoneNumber(
                request.getPhoneNumber()
        );

        subscription.setPlanId(
                request.getPlanId()
        );

        /*
         * New subscription starts as ACTIVE.
         */
        subscription.setStatus(
                SubscriptionStatus.ACTIVE
        );

        subscription.setStartDate(
                request.getStartDate()
        );


        /*
         * Save to subscription_db.
         */
        Subscription savedSubscription =
                subscriptionRepository.save(
                        subscription
                );

        return mapToResponse(
                savedSubscription
        );
    }


    /*
     * ========================================================
     * GET SUBSCRIPTION BY ID
     * ========================================================
     */
    @Transactional(readOnly = true)
    public SubscriptionResponse getSubscriptionById(
            Long id) {

        Subscription subscription =
                subscriptionRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Subscription not found with id: "
                                                + id
                                )
                        );

        return mapToResponse(
                subscription
        );
    }


    /*
     * ========================================================
     * GET CUSTOMER SUBSCRIPTION HISTORY
     * ========================================================
     *
     * Returns all subscriptions belonging to a customer,
     * including historical subscriptions.
     */
    @Transactional(readOnly = true)
    public List<SubscriptionResponse>
            getSubscriptionsByCustomerId(
                    Long customerId) {

        return subscriptionRepository
                .findByCustomerId(customerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    /*
     * ========================================================
     * GET PHONE NUMBER HISTORY
     * ========================================================
     *
     * Example:
     *
     * 9876543210
     *     ├── POSTPAID100 → CANCELLED
     *     ├── POSTPAID200 → CANCELLED
     *     └── UNLIMITED399 → ACTIVE
     */
    @Transactional(readOnly = true)
    public List<SubscriptionResponse>
            getSubscriptionHistoryByPhoneNumber(
                    String phoneNumber) {

        return subscriptionRepository
                .findByPhoneNumber(phoneNumber)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    /*
     * ========================================================
     * GET CURRENT ACTIVE SUBSCRIPTION
     * ========================================================
     */
    @Transactional(readOnly = true)
    public SubscriptionResponse
            getActiveSubscriptionByPhoneNumber(
                    String phoneNumber) {

        Subscription subscription =
                subscriptionRepository
                        .findByPhoneNumberAndStatus(
                                phoneNumber,
                                SubscriptionStatus.ACTIVE
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "No active subscription found "
                                                + "for phone number: "
                                                + phoneNumber
                                )
                        );

        return mapToResponse(
                subscription
        );
    }


    /*
     * ========================================================
     * SUSPEND SUBSCRIPTION
     * ========================================================
     *
     * ACTIVE → SUSPENDED
     */
    @Transactional
    public SubscriptionResponse suspendSubscription(
            Long id) {

        Subscription subscription =
                getSubscriptionEntity(id);

        if (subscription.getStatus()
                != SubscriptionStatus.ACTIVE) {

            throw new InvalidSubscriptionStateException(
                    "Only an ACTIVE subscription can be suspended"
            );
        }

        subscription.setStatus(
                SubscriptionStatus.SUSPENDED
        );

        Subscription updatedSubscription =
                subscriptionRepository.save(
                        subscription
                );

        return mapToResponse(
                updatedSubscription
        );
    }


    /*
     * ========================================================
     * RESUME SUBSCRIPTION
     * ========================================================
     *
     * SUSPENDED → ACTIVE
     */
    @Transactional
    public SubscriptionResponse resumeSubscription(
            Long id) {

        Subscription subscription =
                getSubscriptionEntity(id);

        if (subscription.getStatus()
                != SubscriptionStatus.SUSPENDED) {

            throw new InvalidSubscriptionStateException(
                    "Only a SUSPENDED subscription can be resumed"
            );
        }

        subscription.setStatus(
                SubscriptionStatus.ACTIVE
        );

        Subscription updatedSubscription =
                subscriptionRepository.save(
                        subscription
                );

        return mapToResponse(
                updatedSubscription
        );
    }


    /*
     * ========================================================
     * CANCEL SUBSCRIPTION
     * ========================================================
     *
     * ACTIVE/SUSPENDED → CANCELLED
     *
     * We don't delete the record because subscription
     * history is important for future billing.
     */
    @Transactional
    public SubscriptionResponse cancelSubscription(
            Long id) {

        Subscription subscription =
                getSubscriptionEntity(id);

        if (subscription.getStatus()
                == SubscriptionStatus.CANCELLED) {

            throw new InvalidSubscriptionStateException(
                    "Subscription is already cancelled"
            );
        }

        if (subscription.getStatus()
                == SubscriptionStatus.EXPIRED) {

            throw new InvalidSubscriptionStateException(
                    "Expired subscription cannot be cancelled"
            );
        }

        subscription.setStatus(
                SubscriptionStatus.CANCELLED
        );

        /*
         * Record the date on which the subscription ended.
         */
        subscription.setEndDate(
                LocalDate.now()
        );

        Subscription updatedSubscription =
                subscriptionRepository.save(
                        subscription
                );

        return mapToResponse(
                updatedSubscription
        );
    }


    /*
     * ========================================================
     * INTERNAL HELPER
     * ========================================================
     *
     * Fetch a subscription or throw 404.
     */
    private Subscription getSubscriptionEntity(
            Long id) {

        return subscriptionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Subscription not found with id: "
                                        + id
                        )
                );
    }


    /*
     * ========================================================
     * ENTITY → DTO MAPPING
     * ========================================================
     *
     * We don't expose the JPA entity directly through
     * the REST API.
     */
    private SubscriptionResponse mapToResponse(
            Subscription subscription) {

        return new SubscriptionResponse(
                subscription.getId(),
                subscription.getCustomerId(),
                subscription.getPhoneNumber(),
                subscription.getPlanId(),
                subscription.getStatus(),
                subscription.getStartDate(),
                subscription.getEndDate(),
                subscription.getCreatedAt(),
                subscription.getUpdatedAt()
        );
    }
}