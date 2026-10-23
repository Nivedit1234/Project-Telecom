package com.telecom.orderservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import com.telecom.orderservice.exception.DownstreamServiceException;
import com.telecom.orderservice.exception.InvalidOrderException;
import com.telecom.orderservice.security.AuthorizationHeaderUtil;

@Component
public class CatalogServiceClient {

    private final RestClient restClient;
    private final AuthorizationHeaderUtil authorizationHeaderUtil;

    public CatalogServiceClient(
            RestClient.Builder restClientBuilder,
            AuthorizationHeaderUtil authorizationHeaderUtil,
            @Value("${services.catalog-service.url}")
            String catalogServiceUrl) {

        this.restClient = restClientBuilder
                .baseUrl(catalogServiceUrl)
                .build();

        this.authorizationHeaderUtil = authorizationHeaderUtil;
    }

    public PlanDetails getPlan(Long planId) {

        try {

            return restClient.get()
                    .uri("/api/plans/{id}", planId)
                    .header(
                            HttpHeaders.AUTHORIZATION,
                            authorizationHeaderUtil
                                    .getAuthorizationHeader())
                    .retrieve()
                    .body(PlanDetails.class);

        } catch (HttpClientErrorException.NotFound ex) {

            throw new InvalidOrderException(
                    "Plan not found with id: " + planId);

        } catch (HttpClientErrorException ex) {

            System.out.println(
                    "Catalog Service HTTP status: "
                            + ex.getStatusCode());

            System.out.println(
                    "Catalog Service response body: "
                            + ex.getResponseBodyAsString());

            throw new InvalidOrderException(
                    "Plan validation failed for id: " + planId);

        } catch (ResourceAccessException ex) {

            throw new DownstreamServiceException(
                    "Catalog Service is unavailable");
        }
    }

    public record PlanDetails(
            Long id,
            String code,
            String name,
            String description,
            Double monthlyPrice,
            Double callRatePerMinute,
            Double dataRatePerMB,
            Double smsRate,
            Integer validityDays,
            Boolean active) {
    }
}