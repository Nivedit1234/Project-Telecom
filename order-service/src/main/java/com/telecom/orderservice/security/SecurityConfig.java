package com.telecom.orderservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter
            jwtAuthenticationFilter;

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
                    SessionCreationPolicy.STATELESS))

            .exceptionHandling(exception ->
                exception
                    .authenticationEntryPoint(
                        (request, response, authException) ->
                            response.sendError(
                                401,
                                "Authentication required"))

                    .accessDeniedHandler(
                        (request, response,
                         accessDeniedException) ->
                            response.sendError(
                                403,
                                "Access denied")))

            .authorizeHttpRequests(auth -> auth

                .requestMatchers(
                    "/actuator/health")
                .permitAll()

                .requestMatchers(
                    "/api/orders/**")
                .authenticated()

                .anyRequest()
                .authenticated())

            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {

        return username -> {
            throw new UsernameNotFoundException(
                    "JWT authentication is used");
        };
    }
}