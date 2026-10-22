package com.telecom.billingservice.messaging;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.telecom.billingservice.dto.BillingGeneratedEvent;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class BillingKafkaProducer {

    private static final String BILLING_EVENTS_TOPIC = "billing-events";

    private final KafkaTemplate<String, BillingGeneratedEvent> kafkaTemplate;

    public BillingKafkaProducer(
            KafkaTemplate<String, BillingGeneratedEvent> kafkaTemplate) {

        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishBillingGenerated(BillingGeneratedEvent event) {

        kafkaTemplate.send(
                BILLING_EVENTS_TOPIC,
                String.valueOf(event.billingId()),
                event
        ).whenComplete((result, exception) -> {

            if (exception != null) {

                log.error(
                        "Failed to publish BillingGeneratedEvent for billingId={}",
                        event.billingId(),
                        exception
                );

                return;
            }

            log.info(
                    "BillingGeneratedEvent published successfully: billingId={}, topic={}, partition={}",
                    event.billingId(),
                    result.getRecordMetadata().topic(),
                    result.getRecordMetadata().partition()
            );
        });
    }
}