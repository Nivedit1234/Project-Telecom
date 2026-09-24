package com.telecom.subscriptionservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ServiceClientConfig {

    /*
     * Create a reusable WebClient.Builder.
     *
     * Other classes such as UserServiceClient and
     * CatalogServiceClient can inject this builder
     * and create WebClient instances from it.
     */
    @Bean
    public WebClient.Builder webClientBuilder() {

        return WebClient.builder();
    }
}