package com.telecom.subscriptionservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class CatalogServiceClient {

    private final WebClient webClient;
    private final HttpServletRequest request;

    public CatalogServiceClient(
            WebClient.Builder webClientBuilder,
            HttpServletRequest request,
            @Value("${services.catalog-service.url}") String catalogServiceUrl) {

        this.webClient = webClientBuilder
                .baseUrl(catalogServiceUrl)
                .build();

        this.request = request;
    }

    public void validatePlan(Long planId) {

        String authorizationHeader =
                request.getHeader(HttpHeaders.AUTHORIZATION);

        System.out.println(
                "Catalog call - Authorization header present: "
                        + (authorizationHeader != null));

        System.out.println(
                "Catalog call - Bearer header: "
                        + (authorizationHeader != null
                        && authorizationHeader.startsWith("Bearer ")));

        webClient.get()
                .uri("/api/plans/{id}", planId)
                .header(
                        HttpHeaders.AUTHORIZATION,
                        authorizationHeader)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}