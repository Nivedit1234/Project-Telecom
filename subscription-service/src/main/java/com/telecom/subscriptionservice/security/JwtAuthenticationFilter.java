package com.telecom.subscriptionservice.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(
            JwtService jwtService) {

        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader =
                request.getHeader("Authorization");

        /*
         * No JWT supplied.
         *
         * Do not authenticate the request.
         * SecurityConfig will decide whether authentication
         * is required for the requested endpoint.
         */
        if (authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        String token =
                authorizationHeader.substring(7);

        /*
         * Validate the JWT before creating the
         * Spring Security Authentication object.
         */
        if (jwtService.isTokenValid(token)) {

            try {

                Claims claims =
                        jwtService.extractAllClaims(token);

                String subject =
                        claims.getSubject();

                String role =
                        claims.get("role", String.class);

                List<SimpleGrantedAuthority> authorities =
                        role != null
                                ? List.of(
                                    new SimpleGrantedAuthority(
                                        "ROLE_" + role))
                                : List.of();

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                subject,
                                null,
                                authorities);

                /*
                 * Store the authenticated user in the
                 * current Spring Security context.
                 */
                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);

            } catch (Exception exception) {

                /*
                 * Never allow malformed claims to create
                 * an authenticated security context.
                 */
                SecurityContextHolder.clearContext();
            }
        }

        /*
         * Continue the request.
         *
         * If authentication was not established,
         * SecurityConfig will return 401 for protected APIs.
         */
        filterChain.doFilter(request, response);
    }
}