package com.telecom.orderservice.security;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class AuthorizationHeaderUtil {

    private final HttpServletRequest request;

    public AuthorizationHeaderUtil(
            HttpServletRequest request) {

        this.request = request;
    }

    public String getAuthorizationHeader() {

        return request.getHeader(
                HttpHeaders.AUTHORIZATION);
    }
}