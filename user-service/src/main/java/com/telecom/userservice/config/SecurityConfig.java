package com.telecom.userservice.config;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.telecom.userservice.security.JwtAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder,
            JwtAuthenticationFilter jwtAuthenticationFilter) {

        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(
                        userDetailsService
                );

        provider.setPasswordEncoder(
                passwordEncoder
        );

        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            .exceptionHandling(exception ->
                exception.authenticationEntryPoint(
                    (request, response, authException) -> {

                        response.sendError(
                            HttpServletResponse.SC_UNAUTHORIZED,
                            "Authentication required"
                        );
                    }
                )
            )

            .authorizeHttpRequests(auth -> auth

                // Registration and login are public
                .requestMatchers("/api/auth/**")
                .permitAll()

                // Health check is public
                .requestMatchers("/actuator/health")
                .permitAll()

                // Customers can view customer data
                // Admins can also view customer data
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/customers/**"
                )
                .hasAnyRole("CUSTOMER", "ADMIN")

                // Only admins can create customers
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/customers/**"
                )
                .hasRole("ADMIN")

                // Only admins can update customers
                .requestMatchers(
                    HttpMethod.PUT,
                    "/api/customers/**"
                )
                .hasRole("ADMIN")

                // Only admins can delete customers
                .requestMatchers(
                    HttpMethod.DELETE,
                    "/api/customers/**"
                )
                .hasRole("ADMIN")

                // Anything else requires authentication
                .anyRequest()
                .authenticated()
            )

            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}