package com.telecom.billingservice.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class InternalJwtService {

    private final SecretKey secretKey;

    public InternalJwtService(
            @Value("${jwt.secret}") String secret) {

        this.secretKey = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    public String generateServiceToken() {

        Date issuedAt = new Date();

        Date expiration = new Date(
                issuedAt.getTime() + (5 * 60 * 1000)
        );

        return Jwts.builder()
                .subject("billing-service")
                .claim("role", "ADMIN")
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }
}