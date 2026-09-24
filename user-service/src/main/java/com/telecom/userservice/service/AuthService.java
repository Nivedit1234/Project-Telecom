package com.telecom.userservice.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.telecom.userservice.dto.AuthResponse;
import com.telecom.userservice.dto.LoginRequest;
import com.telecom.userservice.dto.RegisterRequest;
import com.telecom.userservice.model.Customer;
import com.telecom.userservice.model.User;
import com.telecom.userservice.model.UserRole;
import com.telecom.userservice.repository.CustomerRepository;
import com.telecom.userservice.repository.UserRepository;
import com.telecom.userservice.security.JwtService;
import com.telecom.userservice.exception.DuplicateResourceException;
import com.telecom.userservice.exception.DuplicateResourceException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            CustomerRepository customerRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {

        // Check whether the email already belongs to a user
        if (userRepository.existsByEmail(request.getEmail())) {
        	throw new DuplicateResourceException(
        	        "User with this email already exists");
        }

        // Create authentication/identity record
        User user = new User();

        user.setEmail(request.getEmail());
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );
        user.setRole(UserRole.CUSTOMER);
        user.setEnabled(true);

        user = userRepository.save(user);

        // Create customer billing profile
        Customer customer = new Customer();

        customer.setUser(user);
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setBillingAddress(request.getBillingAddress());

        customerRepository.save(customer);

        // Generate JWT tokens
        String accessToken =
                jwtService.generateAccessToken(user);

        String refreshToken =
                jwtService.generateRefreshToken(user);

        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer",
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new BadCredentialsException(
                                "Invalid email or password"
                        )
                );

        // Check whether the account is enabled
        if (!user.isEnabled()) {
            throw new BadCredentialsException(
                    "User account is disabled"
            );
        }

        // Verify password
        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new BadCredentialsException(
                    "Invalid email or password"
            );
        }

        // Generate JWT tokens
        String accessToken =
                jwtService.generateAccessToken(user);

        String refreshToken =
                jwtService.generateRefreshToken(user);

        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer",
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}