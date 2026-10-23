package com.telecom.usageservice.messaging;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.telecom.usageservice.dto.UsageEvent;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class UsageKafkaProducer {

    private static final String TOPIC = "usage-events";

    private final KafkaTemplate<String, UsageEvent> kafkaTemplate;

    public UsageKafkaProducer(KafkaTemplate<String, UsageEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(UsageEvent event) {
        kafkaTemplate.send(TOPIC, String.valueOf(event.mobileLineId()), event)
                .whenComplete((result, exception) -> {

                    if (exception != null) {
                        log.error(
                                "Failed to publish usage event: mobileLineId={}, usageType={}",
                                event.mobileLineId(),
                                event.usageType(),
                                exception
                        );
                        return;
                    }

                    log.info(
                            "Usage event published: topic={}, partition={}, offset={}, mobileLineId={}, usageType={}",
                            result.getRecordMetadata().topic(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset(),
                            event.mobileLineId(),
                            event.usageType()
                    );
                });
    }
}