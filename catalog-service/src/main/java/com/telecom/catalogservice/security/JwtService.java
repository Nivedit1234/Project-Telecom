package com.telecom.catalogservice.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final SecretKey signingKey;

    public JwtService(
            @Value("${jwt.secret}") String secret) {

        /*
         * The JWT secret is shared with User Service and
         * Subscription Service.
         *
         * All services must interpret the secret in exactly
         * the same way. User Service generates JWTs using
         * this key representation, so Catalog Service must
         * use the same UTF-8 byte representation when
         * validating them.
         */
        this.signingKey =
                Keys.hmacShaKeyFor(
                        secret.getBytes(StandardCharsets.UTF_8)
                );
    }

    /*
     * Parse and cryptographically validate the JWT.
     *
     * This validates the JWT signature and standard claims
     * such as expiration.
     */
    public Claims extractClaims(String token) {

        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /*
     * Extract the authenticated user's email.
     */
    public String extractUsername(String token) {

        return extractClaims(token)
                .getSubject();
    }

    /*
     * Extract CUSTOMER / ADMIN role.
     */
    public String extractRole(String token) {

        return extractClaims(token)
                .get("role", String.class);
    }

    /*
     * Extract User Service user ID.
     */
    public Long extractUserId(String token) {

        return extractClaims(token)
                .get("userId", Long.class);
    }

    /*
     * Validate signature, subject and expiration.
     */
    public boolean isTokenValid(String token) {

        try {

            Claims claims = extractClaims(token);

            return claims.getSubject() != null
                    && claims.getExpiration() != null
                    && claims.getExpiration()
                            .after(new Date());

        } catch (Exception exception) {

            return false;
        }
    }
}