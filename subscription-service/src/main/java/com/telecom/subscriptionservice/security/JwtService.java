package com.telecom.subscriptionservice.security;

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

    private final SecretKey secretKey;

    public JwtService(
            @Value("${jwt.secret}") String secret) {

        /*
         * The same JWT secret must be used by every service
         * that validates User Service JWTs.
         *
         * Never log the actual secret.
         */
        this.secretKey = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8));
    }

    /*
     * Extract all claims after validating:
     *
     * 1. JWT signature
     * 2. JWT structure
     * 3. JWT expiration
     */
    public Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /*
     * Validate the JWT.
     */
    public boolean isTokenValid(String token) {

        try {

            Claims claims = extractAllClaims(token);

            /*
             * Explicitly verify that the token has not expired.
             *
             * parseSignedClaims() already performs expiration
             * validation, but keeping this check makes the
             * authentication rule explicit.
             */
            Date expiration = claims.getExpiration();

            return expiration != null
                    && expiration.after(new Date());

        } catch (Exception exception) {

            /*
             * Do not expose JWT details to the HTTP client.
             *
             * Authentication failures simply result in the
             * request remaining unauthenticated.
             */
            return false;
        }
    }
}