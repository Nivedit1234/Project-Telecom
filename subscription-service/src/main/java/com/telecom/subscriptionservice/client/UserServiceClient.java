package com.telecom.subscriptionservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class UserServiceClient {

    private final WebClient webClient;
    private final HttpServletRequest request;

    public UserServiceClient(
            WebClient.Builder webClientBuilder,
            HttpServletRequest request,
            @Value("${services.user-service.url}") String userServiceUrl) {

        this.webClient = webClientBuilder
                .baseUrl(userServiceUrl)
                .build();

        this.request = request;
    }

    public void validateCustomer(Long customerId) {

        String authorizationHeader =
                request.getHeader(HttpHeaders.AUTHORIZATION);

        webClient.get()
                .uri("/api/customers/{id}/validate", customerId)
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}