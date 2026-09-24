package com.telecom.catalogservice.config;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.telecom.catalogservice.security.JwtAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter) {

        this.jwtAuthenticationFilter =
                jwtAuthenticationFilter;
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
                exception
                    .authenticationEntryPoint(
                        (request, response, authException) -> {

                            response.sendError(
                                HttpServletResponse.SC_UNAUTHORIZED,
                                "Authentication required"
                            );
                        }
                    )
                    .accessDeniedHandler(
                        (request, response, accessDeniedException) -> {

                            response.sendError(
                                HttpServletResponse.SC_FORBIDDEN,
                                "Access denied"
                            );
                        }
                    )
            )

            .authorizeHttpRequests(auth -> auth

                // Health check
                .requestMatchers(
                    "/actuator/health"
                ).permitAll()

                // Read plans
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/plans",
                    "/api/plans/**"
                ).hasAnyRole(
                    "CUSTOMER",
                    "ADMIN"
                )

                // Create plan
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/plans"
                ).hasRole("ADMIN")

                // Update plan
                .requestMatchers(
                    HttpMethod.PUT,
                    "/api/plans/**"
                ).hasRole("ADMIN")

                // Deactivate plan
                .requestMatchers(
                    HttpMethod.DELETE,
                    "/api/plans/**"
                ).hasRole("ADMIN")

                // Everything else
                .anyRequest().authenticated()
            )

            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}