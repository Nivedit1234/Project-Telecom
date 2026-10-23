package com.telecom.orderservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.telecom.orderservice.exception.DownstreamServiceException;
import com.telecom.orderservice.exception.InvalidOrderException;
import com.telecom.orderservice.security.AuthorizationHeaderUtil;

@Component
public class UserServiceClient {

    private final RestClient restClient;
    private final AuthorizationHeaderUtil
            authorizationHeaderUtil;

    public UserServiceClient(
            RestClient.Builder restClientBuilder,
            AuthorizationHeaderUtil authorizationHeaderUtil,
            @Value("${services.user-service.url}")
            String userServiceUrl) {

        this.restClient =
                restClientBuilder
                        .baseUrl(userServiceUrl)
                        .build();

        this.authorizationHeaderUtil =
                authorizationHeaderUtil;
    }

    public CustomerDetails getCustomer(
            Long customerId) {

        try {

            return restClient.get()
                    .uri(
                        "/api/customers/{id}",
                        customerId)
                    .header(
                        HttpHeaders.AUTHORIZATION,
                        authorizationHeaderUtil
                            .getAuthorizationHeader())
                    .retrieve()
                    .body(CustomerDetails.class);

        } catch (
                org.springframework.web.client
                .HttpClientErrorException.NotFound ex) {

            throw new InvalidOrderException(
                    "Customer not found with id: "
                            + customerId);

        } catch (
                org.springframework.web.client
                .HttpClientErrorException ex) {

            throw new InvalidOrderException(
                    "Customer validation failed for id: "
                            + customerId);

        } catch (
                org.springframework.web.client
                .ResourceAccessException ex) {

            throw new DownstreamServiceException(
                    "User Service is unavailable");
        }
    }

    public record CustomerDetails(
            Long id,
            Long userId,
            String name,
            String email,
            String billingAddress) {
    }
}