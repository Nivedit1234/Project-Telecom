package com.telecom.billingservice.config;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.telecom.billingservice.security.InternalJwtService;

@Component
public class AuthorizationHeaderUtil {

    private final InternalJwtService internalJwtService;

    public AuthorizationHeaderUtil(
            InternalJwtService internalJwtService) {

        this.internalJwtService = internalJwtService;
    }

    public String getAuthorizationHeader() {

        ServletRequestAttributes attributes =
                (ServletRequestAttributes)
                        RequestContextHolder.getRequestAttributes();

        if (attributes != null) {

            String authorizationHeader =
                    attributes.getRequest()
                            .getHeader(HttpHeaders.AUTHORIZATION);

            if (authorizationHeader != null
                    && !authorizationHeader.isBlank()) {

                return authorizationHeader;
            }
        }

        /*
         * Scheduler/background execution has no HTTP request.
         * Generate a short-lived service JWT instead.
         */
        return "Bearer "
                + internalJwtService.generateServiceToken();
    }
}