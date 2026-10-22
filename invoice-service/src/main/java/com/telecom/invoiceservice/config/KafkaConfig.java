package com.telecom.invoiceservice.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import com.telecom.invoiceservice.dto.BillingGeneratedEvent;

@Configuration
public class KafkaConfig {

    @Bean
    public ConsumerFactory<String, BillingGeneratedEvent> consumerFactory() {

        Map<String, Object> properties = new HashMap<>();

        properties.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "localhost:9092"
        );

        properties.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                "invoice-processing-group"
        );

        properties.put(
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                "earliest"
        );

        properties.put(
                ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
                false
        );

        properties.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );

        properties.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                JacksonJsonDeserializer.class
        );

        properties.put(
                JacksonJsonDeserializer.TRUSTED_PACKAGES,
                "com.telecom.billingservice.dto,com.telecom.invoiceservice.dto"
        );

        properties.put(
                JacksonJsonDeserializer.VALUE_DEFAULT_TYPE,
                BillingGeneratedEvent.class.getName()
        );

        properties.put(
                JacksonJsonDeserializer.USE_TYPE_INFO_HEADERS,
                false
        );

        return new DefaultKafkaConsumerFactory<>(properties);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, BillingGeneratedEvent>
            kafkaListenerContainerFactory(
                    ConsumerFactory<String, BillingGeneratedEvent> consumerFactory) {

        ConcurrentKafkaListenerContainerFactory<String, BillingGeneratedEvent>
                factory = new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);

        factory.setCommonErrorHandler(kafkaErrorHandler());

        return factory;
    }

    @Bean
    public CommonErrorHandler kafkaErrorHandler() {

        return new DefaultErrorHandler(
                new FixedBackOff(1000L, 1L)
        );
    }
}