package com.telecom.subscriptionservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration for Subscription Service.
 *
 * Subscription APIs require a valid JWT.
 *
 * Actuator health is kept publicly accessible so that
 * Docker/AWS/load-balancer health checks can use it.
 */
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter) {

        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            /*
             * JWT authentication is stateless.
             * The server does not maintain HTTP sessions.
             */
            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS))

            .authorizeHttpRequests(auth -> auth

                // Health endpoint is required for infrastructure checks.
                .requestMatchers("/actuator/health").permitAll()

                // Every Subscription API requires authentication.
                .requestMatchers("/api/subscriptions/**").authenticated()

                // Everything else is also protected by default.
                .anyRequest().authenticated()
            )

            /*
             * Run our JWT filter before Spring Security's
             * username/password authentication filter.
             */
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}