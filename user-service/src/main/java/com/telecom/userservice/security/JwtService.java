package com.telecom.userservice.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.telecom.userservice.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long accessTokenExpiration,
            @Value("${jwt.refresh-expiration}") long refreshTokenExpiration) {

        /*
         * This is the secret used to SIGN access and refresh tokens.
         *
         * Catalog Service and Subscription Service must use the
         * exact same JWT_SECRET when they validate these tokens.
         */
        this.secretKey = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8));

        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    /**
     * Generates the access token used for API authentication.
     */
    public String generateAccessToken(User user) {

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("role", user.getRole().name())
                .claim("userId", user.getId())
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + accessTokenExpiration))
                .signWith(secretKey)
                .compact();
    }

    /**
     * Generates the refresh token.
     *
     * The refresh token is used to obtain a new access token
     * when the access token expires.
     */
    public String generateRefreshToken(User user) {

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId", user.getId())
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + refreshTokenExpiration))
                .signWith(secretKey)
                .compact();
    }

    /**
     * Extracts the email/username from the JWT.
     */
    public String extractUsername(String token) {

        return extractAllClaims(token)
                .getSubject();
    }

    /**
     * Validates that the token belongs to the expected user
     * and that the signature/expiration are valid.
     */
    public boolean isTokenValid(
            String token,
            String email) {

        try {

            String username = extractUsername(token);

            return username.equals(email)
                    && !isTokenExpired(token);

        } catch (Exception ex) {

            return false;
        }
    }

    /**
     * Checks whether the JWT has expired.
     */
    private boolean isTokenExpired(String token) {

        return extractAllClaims(token)
                .getExpiration()
                .before(new Date());
    }

    /**
     * Parses and verifies the JWT signature.
     */
    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}