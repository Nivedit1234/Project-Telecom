package com.telecom.userservice.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.telecom.userservice.dto.CustomerRequest;
import com.telecom.userservice.dto.CustomerResponse;
import com.telecom.userservice.exception.DuplicateResourceException;
import com.telecom.userservice.exception.ResourceNotFoundException;
import com.telecom.userservice.model.Customer;
import com.telecom.userservice.model.User;
import com.telecom.userservice.repository.CustomerRepository;
import com.telecom.userservice.repository.UserRepository;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    public CustomerService(
            CustomerRepository customerRepository,
            UserRepository userRepository) {

        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
    }

    /*
     * Create a customer profile for an existing user.
     *
     * This method is currently intended for administrative use.
     * Normal customer registration is handled by AuthService.
     */
    @Transactional
    public CustomerResponse createCustomer(
            CustomerRequest request,
            Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + userId));

        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "Customer with this email already exists");
        }

        Customer customer = new Customer();

        customer.setUser(user);
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setBillingAddress(request.getBillingAddress());

        Customer savedCustomer =
                customerRepository.save(customer);

        return mapToResponse(savedCustomer);
    }

    /*
     * Get customer by customer ID.
     */
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(Long id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer not found with id: " + id));

        return mapToResponse(customer);
    }

    /*
     * Validate that a customer belongs to the
     * currently authenticated user.
     *
     * This is used by other services when processing
     * customer-originated requests.
     *
     * No customer data is returned. A successful method
     * completion means the ownership check passed.
     */
    @Transactional(readOnly = true)
    public void validateCustomerOwnership(
            Long customerId,
            String authenticatedEmail) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer not found with id: "
                                        + customerId));

        String customerUserEmail =
                customer.getUser().getEmail();

        if (!customerUserEmail.equalsIgnoreCase(
                authenticatedEmail)) {

            throw new org.springframework.security.access.AccessDeniedException(
                    "Authenticated user does not own this customer");
        }
    }

    /*
     * Get all customer profiles.
     */
    @Transactional(readOnly = true)
    public List<CustomerResponse> getAllCustomers() {

        return customerRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /*
     * Get the customer profile belonging to
     * the currently authenticated user.
     */
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerByUserEmail(
            String email) {

        Customer customer = customerRepository
                .findByUserEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer profile not found"));

        return mapToResponse(customer);
    }

    /*
     * Update customer profile.
     */
    @Transactional
    public CustomerResponse updateCustomer(
            Long id,
            CustomerRequest request) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer not found with id: " + id));

        if (customerRepository.existsByEmailAndIdNot(
                request.getEmail(), id)) {

            throw new DuplicateResourceException(
                    "Customer with this email already exists");
        }

        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setBillingAddress(
                request.getBillingAddress());

        Customer updatedCustomer =
                customerRepository.save(customer);

        return mapToResponse(updatedCustomer);
    }

    /*
     * Delete customer profile.
     */
    @Transactional
    public void deleteCustomer(Long id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer not found with id: " + id));

        customerRepository.delete(customer);
    }

    /*
     * Convert Customer entity to CustomerResponse.
     *
     * Password, role and other authentication details
     * are deliberately not exposed.
     */
    private CustomerResponse mapToResponse(
            Customer customer) {

        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getBillingAddress(),
                customer.getUser().getId(),
                customer.getCreatedAt(),
                customer.getUpdatedAt()
        );
    }
}