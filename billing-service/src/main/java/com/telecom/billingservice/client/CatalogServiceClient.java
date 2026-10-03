package com.telecom.billingservice.client;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.telecom.billingservice.config.AuthorizationHeaderUtil;
import com.telecom.billingservice.exception.DownstreamServiceException;

@Component
public class CatalogServiceClient {

    private final RestClient restClient;
    private final AuthorizationHeaderUtil authorizationHeaderUtil;

    public CatalogServiceClient(
            RestClient.Builder restClientBuilder,
            AuthorizationHeaderUtil authorizationHeaderUtil,
            @Value("${services.catalog-service.url}") String catalogServiceUrl) {

        this.restClient = restClientBuilder
                .baseUrl(catalogServiceUrl)
                .build();

        this.authorizationHeaderUtil = authorizationHeaderUtil;
    }

    public PlanDetails getPlanById(Long planId) {

        try {
            String authorizationHeader =
                    authorizationHeaderUtil.getAuthorizationHeader();

            RestClient.RequestHeadersSpec<?> request =
                    restClient.get()
                            .uri("/api/plans/{id}", planId);

            if (authorizationHeader != null
                    && !authorizationHeader.isBlank()) {

                request.header(
                        HttpHeaders.AUTHORIZATION,
                        authorizationHeader
                );
            }

            return request
                    .retrieve()
                    .onStatus(
                            HttpStatusCode::isError,
                            (responseRequest, response) -> {

                                throw new DownstreamServiceException(
                                        "Catalog Service returned HTTP "
                                                + response.getStatusCode().value()
                                );
                            })
                    .body(PlanDetails.class);

        } catch (DownstreamServiceException exception) {
            throw exception;

        } catch (RestClientException exception) {
            throw new DownstreamServiceException(
                    "Catalog Service is unavailable"
            );
        }
    }

    public record PlanDetails(
            Long id,
            String code,
            String name,
            String description,
            BigDecimal monthlyPrice,
            BigDecimal callRatePerMinute,
            BigDecimal dataRatePerMB,
            BigDecimal smsRate,
            Integer validityDays,
            Boolean active
    ) {
    }
}