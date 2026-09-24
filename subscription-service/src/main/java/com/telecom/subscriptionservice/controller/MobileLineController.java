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

import com.telecom.subscriptionservice.dto.MobileLineRequest;
import com.telecom.subscriptionservice.dto.MobileLineResponse;
import com.telecom.subscriptionservice.service.MobileLineService;

import jakarta.validation.Valid;

/**
 * REST APIs for telecom MobileLines.
 */
@RestController
@RequestMapping("/api/mobile-lines")
public class MobileLineController {

    private final MobileLineService mobileLineService;

    public MobileLineController(
            MobileLineService mobileLineService) {

        this.mobileLineService = mobileLineService;
    }

    /**
     * Provision a new mobile line for a customer.
     *
     * POST /api/mobile-lines
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MobileLineResponse createMobileLine(
            @Valid @RequestBody MobileLineRequest request) {

        return mobileLineService.createMobileLine(request);
    }

    /**
     * Get mobile line by ID.
     */
    @GetMapping("/{id}")
    public MobileLineResponse getMobileLineById(
            @PathVariable Long id) {

        return mobileLineService.getMobileLineById(id);
    }

    /**
     * Get all mobile lines belonging to a customer.
     *
     * This represents the future "My Mobile Numbers" screen.
     */
    @GetMapping("/customer/{customerId}")
    public List<MobileLineResponse> getMobileLinesByCustomerId(
            @PathVariable Long customerId) {

        return mobileLineService
                .getMobileLinesByCustomerId(customerId);
    }

    /**
     * Find a mobile line by phone number.
     */
    @GetMapping("/phone/{phoneNumber}")
    public MobileLineResponse getMobileLineByPhoneNumber(
            @PathVariable String phoneNumber) {

        return mobileLineService
                .getMobileLineByPhoneNumber(phoneNumber);
    }

    /**
     * ACTIVE -> SUSPENDED
     */
    @PostMapping("/{id}/suspend")
    public MobileLineResponse suspendMobileLine(
            @PathVariable Long id) {

        return mobileLineService
                .suspendMobileLine(id);
    }

    /**
     * SUSPENDED -> ACTIVE
     */
    @PostMapping("/{id}/activate")
    public MobileLineResponse activateMobileLine(
            @PathVariable Long id) {

        return mobileLineService
                .activateMobileLine(id);
    }

    /**
     * ACTIVE/SUSPENDED -> DEACTIVATED
     */
    @DeleteMapping("/{id}")
    public MobileLineResponse deactivateMobileLine(
            @PathVariable Long id) {

        return mobileLineService
                .deactivateMobileLine(id);
    }
}