package com.telecom.usageservice.generator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.telecom.usageservice.dto.UsageEvent;
import com.telecom.usageservice.messaging.UsageKafkaProducer;
import com.telecom.usageservice.model.UsageType;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class UsageEventGenerator {

    private final UsageKafkaProducer usageKafkaProducer;

    public UsageEventGenerator(UsageKafkaProducer usageKafkaProducer) {
        this.usageKafkaProducer = usageKafkaProducer;
    }

    @Scheduled(fixedDelay = 10000)
    public void generateUsageEvent() {

        UsageType usageType = randomUsageType();

        BigDecimal quantity = generateQuantity(usageType);

        UsageEvent event = new UsageEvent(
                2L,
                1L,
                1L,
                2L,
                usageType,
                quantity,
                LocalDateTime.now()
        );

        log.info(
                "Generating usage event: mobileLineId={}, usageType={}, quantity={}",
                event.mobileLineId(),
                event.usageType(),
                event.quantity()
        );

        usageKafkaProducer.publish(event);
    }

    private UsageType randomUsageType() {
        UsageType[] types = UsageType.values();

        return types[
                ThreadLocalRandom.current().nextInt(types.length)
        ];
    }

    private BigDecimal generateQuantity(UsageType usageType) {

        return switch (usageType) {

            case CALL -> BigDecimal
                    .valueOf(ThreadLocalRandom.current().nextDouble(0.5, 15.0))
                    .setScale(3, RoundingMode.HALF_UP);

            case DATA -> BigDecimal
                    .valueOf(ThreadLocalRandom.current().nextDouble(10.0, 1000.0))
                    .setScale(3, RoundingMode.HALF_UP);

            case SMS -> BigDecimal
                    .valueOf(ThreadLocalRandom.current().nextInt(1, 6));
        };
    }
}