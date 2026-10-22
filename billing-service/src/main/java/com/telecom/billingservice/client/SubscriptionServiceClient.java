package com.telecom.billingservice.client;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.telecom.billingservice.config.AuthorizationHeaderUtil;
import com.telecom.billingservice.exception.DownstreamServiceException;

@Component
public class SubscriptionServiceClient {

    private final RestClient restClient;
    private final AuthorizationHeaderUtil authorizationHeaderUtil;

    public SubscriptionServiceClient(
            RestClient.Builder restClientBuilder,
            AuthorizationHeaderUtil authorizationHeaderUtil,
            @Value("${services.subscription-service.url}")
            String subscriptionServiceUrl) {

        this.restClient = restClientBuilder
                .baseUrl(subscriptionServiceUrl)
                .build();

        this.authorizationHeaderUtil = authorizationHeaderUtil;
    }

    public List<SubscriptionDetails> getActiveSubscriptions() {

        try {
            String authorizationHeader =
                    authorizationHeaderUtil.getAuthorizationHeader();

            RestClient.RequestHeadersSpec<?> request =
                    restClient.get()
                            .uri(uriBuilder -> uriBuilder
                                    .path("/api/subscriptions/status")
                                    .queryParam("status", "ACTIVE")
                                    .build());

            request.header(
                    HttpHeaders.AUTHORIZATION,
                    authorizationHeader
            );

            List<SubscriptionDetails> subscriptions =
                    request
                            .retrieve()
                            .onStatus(
                                    HttpStatusCode::isError,
                                    (responseRequest, clientResponse) -> {
                                        throw new DownstreamServiceException(
                                                "Subscription Service returned HTTP "
                                                        + clientResponse
                                                                .getStatusCode()
                                                                .value()
                                        );
                                    })
                            .body(
                                    new ParameterizedTypeReference<
                                            List<SubscriptionDetails>>() {
                                    }
                            );

            return subscriptions == null
                    ? List.of()
                    : subscriptions;

        } catch (DownstreamServiceException exception) {

            throw exception;

        } catch (RestClientException exception) {

            throw new DownstreamServiceException(
                    "Subscription Service is unavailable",
                    exception
            );
        }
    }

    public MobileLineDetails getMobileLine(Long mobileLineId) {

        try {
            String authorizationHeader =
                    authorizationHeaderUtil.getAuthorizationHeader();

            RestClient.RequestHeadersSpec<?> request =
                    restClient.get()
                            .uri(
                                    "/api/mobile-lines/{id}",
                                    mobileLineId
                            );

            request.header(
                    HttpHeaders.AUTHORIZATION,
                    authorizationHeader
            );

            return request
                    .retrieve()
                    .onStatus(
                            HttpStatusCode::isError,
                            (responseRequest, clientResponse) -> {
                                throw new DownstreamServiceException(
                                        "Subscription Service returned HTTP "
                                                + clientResponse
                                                        .getStatusCode()
                                                        .value()
                                );
                            })
                    .body(MobileLineDetails.class);

        } catch (DownstreamServiceException exception) {

            throw exception;

        } catch (RestClientException exception) {

            throw new DownstreamServiceException(
                    "Subscription Service is unavailable",
                    exception
            );
        }
    }

    public record SubscriptionDetails(
            Long id,
            Long mobileLineId,
            Long planId,
            String status,
            LocalDate startDate,
            LocalDate endDate,
            String createdAt,
            String updatedAt
    ) {
    }

    public record MobileLineDetails(
            Long id,
            Long customerId,
            String phoneNumber,
            String simNumber,
            String status,
            LocalDate activationDate,
            LocalDate deactivationDate,
            String createdAt,
            String updatedAt
    ) {
    }
}