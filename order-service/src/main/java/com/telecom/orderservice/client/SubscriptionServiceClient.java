package com.telecom.orderservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import com.telecom.orderservice.exception.DownstreamServiceException;
import com.telecom.orderservice.exception.InvalidOrderException;
import com.telecom.orderservice.security.AuthorizationHeaderUtil;

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

    public MobileLineDetails getMobileLine(Long mobileLineId) {

        try {

            return restClient.get()
                    .uri("/api/mobile-lines/{id}", mobileLineId)
                    .header(
                            HttpHeaders.AUTHORIZATION,
                            authorizationHeaderUtil
                                    .getAuthorizationHeader())
                    .retrieve()
                    .body(MobileLineDetails.class);

        } catch (HttpClientErrorException.NotFound ex) {

            throw new InvalidOrderException(
                    "Mobile line not found with id: "
                            + mobileLineId);

        } catch (HttpClientErrorException ex) {

            throw new InvalidOrderException(
                    "Mobile line validation failed for id: "
                            + mobileLineId);

        } catch (HttpServerErrorException ex) {

            throw new DownstreamServiceException(
                    "Subscription Service returned an error");

        } catch (ResourceAccessException ex) {

            throw new DownstreamServiceException(
                    "Subscription Service is unavailable");
        }
    }

    public SubscriptionDetails getSubscription(Long subscriptionId) {

        try {

            return restClient.get()
                    .uri("/api/subscriptions/{id}", subscriptionId)
                    .header(
                            HttpHeaders.AUTHORIZATION,
                            authorizationHeaderUtil
                                    .getAuthorizationHeader())
                    .retrieve()
                    .body(SubscriptionDetails.class);

        } catch (HttpClientErrorException.NotFound ex) {

            throw new InvalidOrderException(
                    "Subscription not found with id: "
                            + subscriptionId);

        } catch (HttpClientErrorException ex) {

            throw new InvalidOrderException(
                    "Subscription validation failed for id: "
                            + subscriptionId);

        } catch (HttpServerErrorException ex) {

            throw new DownstreamServiceException(
                    "Subscription Service returned an error");

        } catch (ResourceAccessException ex) {

            throw new DownstreamServiceException(
                    "Subscription Service is unavailable");
        }
    }

    /**
     * Performs the actual plan change in Subscription Service.
     *
     * This is intentionally different from getSubscription().
     * getSubscription() only validates/reads the current state.
     * changePlan() performs the actual business operation.
     */
    public SubscriptionDetails changePlan(
            Long subscriptionId,
            Long newPlanId) {

        try {

            return restClient.post()
                    .uri(
                            "/api/subscriptions/{id}/change-plan/{planId}",
                            subscriptionId,
                            newPlanId)
                    .header(
                            HttpHeaders.AUTHORIZATION,
                            authorizationHeaderUtil
                                    .getAuthorizationHeader())
                    .retrieve()
                    .body(SubscriptionDetails.class);

        } catch (HttpClientErrorException.NotFound ex) {

            throw new InvalidOrderException(
                    "Subscription or requested plan not found");

        } catch (HttpClientErrorException ex) {

            String responseBody = ex.getResponseBodyAsString();

            if (responseBody != null
                    && !responseBody.isBlank()) {

                throw new InvalidOrderException(
                        "Plan change failed: "
                                + responseBody);
            }

            throw new InvalidOrderException(
                    "Plan change failed for subscription id: "
                            + subscriptionId);

        } catch (HttpServerErrorException ex) {

            throw new DownstreamServiceException(
                    "Subscription Service returned an error while changing the plan");

        } catch (ResourceAccessException ex) {

            throw new DownstreamServiceException(
                    "Subscription Service is unavailable");
        }
    }

    public record MobileLineDetails(
            Long id,
            Long customerId,
            String phoneNumber,
            String simNumber,
            String status) {
    }

    public record SubscriptionDetails(
            Long id,
            Long mobileLineId,
            Long planId,
            String status,
            String startDate,
            String endDate) {
    }
}