package com.telecom.usageservice.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.telecom.usageservice.dto.UsageEvent;
import com.telecom.usageservice.service.UsageService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class UsageKafkaConsumer {

    private final UsageService usageService;

    public UsageKafkaConsumer(UsageService usageService) {
        this.usageService = usageService;
    }

    @KafkaListener(
            topics = "usage-events",
            groupId = "usage-processing-group"
    )
    public void consumeUsageEvent(UsageEvent event) {

        log.info(
                "Received usage event: customerId={}, mobileLineId={}, subscriptionId={}, usageType={}, quantity={}, eventTime={}",
                event.customerId(),
                event.mobileLineId(),
                event.subscriptionId(),
                event.usageType(),
                event.quantity(),
                event.eventTime()
        );

        usageService.processUsageEvent(event);

        log.info(
                "Usage event processed successfully: mobileLineId={}, usageDate={}",
                event.mobileLineId(),
                event.eventTime().toLocalDate()
        );
    }
}