package com.telecom.userservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.telecom.userservice.dto.CustomerRequest;
import com.telecom.userservice.dto.CustomerResponse;
import com.telecom.userservice.service.CustomerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /*
     * Get the currently authenticated customer's profile.
     */
    @GetMapping("/me")
    public CustomerResponse getMyProfile(
            Authentication authentication) {

        String email = authentication.getName();

        return customerService.getCustomerByUserEmail(email);
    }

    /*
     * Validate that the currently authenticated user
     * owns the requested customer profile.
     *
     * This endpoint is intended for authenticated
     * service-to-service flows initiated by a customer.
     *
     * It does NOT expose arbitrary customer information.
     * It only returns HTTP 200 when the authenticated
     * user is allowed to access the customer.
     */
    @GetMapping("/{id}/validate")
    public void validateCustomer(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();

        customerService.validateCustomerOwnership(
                id,
                email
        );
    }

    /*
     * Get customer by ID.
     *
     * This remains a separate endpoint because it is
     * intended for administrative/customer-management
     * operations according to SecurityConfig.
     */
    @GetMapping("/{id}")
    public CustomerResponse getCustomerById(
            @PathVariable Long id) {

        return customerService.getCustomerById(id);
    }

    /*
     * Get all customer profiles.
     * Administrative use case.
     */
    @GetMapping
    public List<CustomerResponse> getAllCustomers() {

        return customerService.getAllCustomers();
    }

    /*
     * Create a customer profile for an existing user.
     * Administrative operation.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponse createCustomer(
            @Valid @RequestBody CustomerRequest request,
            @RequestParam Long userId) {

        return customerService.createCustomer(
                request,
                userId
        );
    }

    /*
     * Update customer profile.
     * Administrative operation.
     */
    @PutMapping("/{id}")
    public CustomerResponse updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequest request) {

        return customerService.updateCustomer(
                id,
                request
        );
    }

    /*
     * Delete customer profile.
     * Administrative operation.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCustomer(
            @PathVariable Long id) {

        customerService.deleteCustomer(id);
    }
}