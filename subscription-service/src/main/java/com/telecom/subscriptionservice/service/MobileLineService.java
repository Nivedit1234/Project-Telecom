package com.telecom.subscriptionservice.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.telecom.subscriptionservice.client.UserServiceClient;
import com.telecom.subscriptionservice.dto.MobileLineRequest;
import com.telecom.subscriptionservice.dto.MobileLineResponse;
import com.telecom.subscriptionservice.exception.DuplicateResourceException;
import com.telecom.subscriptionservice.exception.InvalidMobileLineStateException;
import com.telecom.subscriptionservice.exception.ResourceNotFoundException;
import com.telecom.subscriptionservice.model.MobileLine;
import com.telecom.subscriptionservice.model.MobileLineStatus;
import com.telecom.subscriptionservice.repository.MobileLineRepository;

/**
 * Business logic for telecom mobile lines.
 */
@Service
public class MobileLineService {

    private final MobileLineRepository mobileLineRepository;

    /*
     * Customer data belongs to User Service.
     *
     * We therefore validate the customer through the
     * existing User Service instead of creating a local
     * Customer table in subscription_db.
     */
    private final UserServiceClient userServiceClient;

    public MobileLineService(
            MobileLineRepository mobileLineRepository,
            UserServiceClient userServiceClient) {

        this.mobileLineRepository = mobileLineRepository;
        this.userServiceClient = userServiceClient;
    }

    /**
     * Provision a new telecom mobile line.
     *
     * Flow:
     *
     * 1. Validate customer through User Service.
     * 2. Check phone number uniqueness.
     * 3. Check SIM/ICCID uniqueness.
     * 4. Create ACTIVE MobileLine.
     */
    @Transactional
    public MobileLineResponse createMobileLine(
            MobileLineRequest request) {

        /*
         * Customer belongs to User Service.
         *
         * This also validates that the authenticated user
         * owns the requested customer profile.
         */
        userServiceClient.validateCustomer(
                request.getCustomerId()
        );

        /*
         * Phone number must be globally unique.
         */
        if (mobileLineRepository.existsByPhoneNumber(
                request.getPhoneNumber())) {

            throw new DuplicateResourceException(
                    "Phone number is already assigned: "
                            + request.getPhoneNumber()
            );
        }

        /*
         * SIM/ICCID must also be globally unique.
         */
        if (mobileLineRepository.existsBySimNumber(
                request.getSimNumber())) {

            throw new DuplicateResourceException(
                    "SIM number is already assigned: "
                            + request.getSimNumber()
            );
        }

        MobileLine mobileLine = new MobileLine();

        mobileLine.setCustomerId(
                request.getCustomerId()
        );

        mobileLine.setPhoneNumber(
                request.getPhoneNumber()
        );

        mobileLine.setSimNumber(
                request.getSimNumber()
        );

        mobileLine.setStatus(
                MobileLineStatus.ACTIVE
        );

        mobileLine.setActivationDate(
                LocalDate.now()
        );

        MobileLine savedMobileLine =
                mobileLineRepository.save(mobileLine);

        return mapToResponse(savedMobileLine);
    }

    /**
     * Get a MobileLine by ID.
     */
    @Transactional(readOnly = true)
    public MobileLineResponse getMobileLineById(Long id) {

        MobileLine mobileLine =
                getMobileLineEntity(id);

        return mapToResponse(mobileLine);
    }

    /**
     * Get all mobile lines belonging to a customer.
     *
     * This is the API the future frontend can use to show:
     *
     * "My Mobile Numbers"
     */
    @Transactional(readOnly = true)
    public List<MobileLineResponse> getMobileLinesByCustomerId(
            Long customerId) {

        return mobileLineRepository
                .findByCustomerId(customerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Find a mobile line by phone number.
     */
    @Transactional(readOnly = true)
    public MobileLineResponse getMobileLineByPhoneNumber(
            String phoneNumber) {

        MobileLine mobileLine =
                mobileLineRepository
                        .findByPhoneNumber(phoneNumber)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Mobile line not found for phone number: "
                                                + phoneNumber
                                )
                        );

        return mapToResponse(mobileLine);
    }

    /**
     * ACTIVE -> SUSPENDED
     */
    @Transactional
    public MobileLineResponse suspendMobileLine(Long id) {

        MobileLine mobileLine =
                getMobileLineEntity(id);

        if (mobileLine.getStatus()
                != MobileLineStatus.ACTIVE) {

            throw new InvalidMobileLineStateException(
                    "Only an ACTIVE mobile line can be suspended"
            );
        }

        mobileLine.setStatus(
                MobileLineStatus.SUSPENDED
        );

        MobileLine updatedMobileLine =
                mobileLineRepository.save(mobileLine);

        return mapToResponse(updatedMobileLine);
    }

    /**
     * SUSPENDED -> ACTIVE
     */
    @Transactional
    public MobileLineResponse activateMobileLine(Long id) {

        MobileLine mobileLine =
                getMobileLineEntity(id);

        if (mobileLine.getStatus()
                != MobileLineStatus.SUSPENDED) {

            throw new InvalidMobileLineStateException(
                    "Only a SUSPENDED mobile line can be activated"
            );
        }

        mobileLine.setStatus(
                MobileLineStatus.ACTIVE
        );

        mobileLine.setDeactivationDate(null);

        MobileLine updatedMobileLine =
                mobileLineRepository.save(mobileLine);

        return mapToResponse(updatedMobileLine);
    }

    /**
     * ACTIVE/SUSPENDED -> DEACTIVATED
     *
     * The relationship with the customer is preserved.
     * We never physically delete a telecom line because
     * historical records are important for billing/auditing.
     */
    @Transactional
    public MobileLineResponse deactivateMobileLine(Long id) {

        MobileLine mobileLine =
                getMobileLineEntity(id);

        if (mobileLine.getStatus()
                == MobileLineStatus.DEACTIVATED) {

            throw new InvalidMobileLineStateException(
                    "Mobile line is already deactivated"
            );
        }

        mobileLine.setStatus(
                MobileLineStatus.DEACTIVATED
        );

        mobileLine.setDeactivationDate(
                LocalDate.now()
        );

        MobileLine updatedMobileLine =
                mobileLineRepository.save(mobileLine);

        return mapToResponse(updatedMobileLine);
    }

    /**
     * Internal helper.
     */
    private MobileLine getMobileLineEntity(Long id) {

        return mobileLineRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Mobile line not found with id: "
                                        + id
                        )
                );
    }

    /**
     * Convert entity to API response.
     */
    private MobileLineResponse mapToResponse(
            MobileLine mobileLine) {

        return new MobileLineResponse(
                mobileLine.getId(),
                mobileLine.getCustomerId(),
                mobileLine.getPhoneNumber(),
                mobileLine.getSimNumber(),
                mobileLine.getStatus(),
                mobileLine.getActivationDate(),
                mobileLine.getDeactivationDate(),
                mobileLine.getCreatedAt(),
                mobileLine.getUpdatedAt()
        );
    }
}