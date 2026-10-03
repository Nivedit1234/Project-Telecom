package com.telecom.billingservice.client;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
public class UsageServiceClient {

    private final RestClient restClient;
    private final AuthorizationHeaderUtil authorizationHeaderUtil;

    public UsageServiceClient(
            RestClient.Builder restClientBuilder,
            AuthorizationHeaderUtil authorizationHeaderUtil,
            @Value("${services.usage-service.url}") String usageServiceUrl) {

        this.restClient = restClientBuilder
                .baseUrl(usageServiceUrl)
                .build();

        this.authorizationHeaderUtil = authorizationHeaderUtil;
    }

    public List<DailyUsageDetails> getUsageByMobileLine(
            Long mobileLineId,
            LocalDate startDate,
            LocalDate endDate) {

        try {
            String authorizationHeader =
                    authorizationHeaderUtil.getAuthorizationHeader();

            RestClient.RequestHeadersSpec<?> request =
                    restClient.get()
                            .uri(uriBuilder -> uriBuilder
                                    .path("/api/usage/mobile-line/{mobileLineId}")
                                    .queryParam("startDate", startDate)
                                    .queryParam("endDate", endDate)
                                    .build(mobileLineId));

            if (authorizationHeader != null
                    && !authorizationHeader.isBlank()) {

                request.header(
                        HttpHeaders.AUTHORIZATION,
                        authorizationHeader
                );
            }

            List<DailyUsageDetails> usageResponse =
                    request
                            .retrieve()
                            .onStatus(
                                    HttpStatusCode::isError,
                                    (responseRequest, response) -> {

                                        throw new DownstreamServiceException(
                                                "Usage Service returned HTTP "
                                                        + response.getStatusCode().value()
                                        );
                                    })
                            .body(
                                    new ParameterizedTypeReference<
                                            List<DailyUsageDetails>>() {
                                    }
                            );

            return usageResponse == null
                    ? List.of()
                    : usageResponse;

        } catch (DownstreamServiceException exception) {
            throw exception;

        } catch (RestClientException exception) {
            throw new DownstreamServiceException(
                    "Usage Service is unavailable"
            );
        }
    }

    public record DailyUsageDetails(
            Long id,
            Long customerId,
            Long mobileLineId,
            Long subscriptionId,
            Long planId,
            LocalDate usageDate,
            BigDecimal callMinutes,
            BigDecimal dataMb,
            Long smsCount,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
    }
}