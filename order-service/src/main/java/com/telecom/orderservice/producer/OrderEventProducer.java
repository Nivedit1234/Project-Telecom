package com.telecom.orderservice.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.telecom.orderservice.event.OrderCompletedEvent;

@Component
public class OrderEventProducer {

    private static final String TOPIC = "order-events";

    private final KafkaTemplate<String, OrderCompletedEvent> kafkaTemplate;

    public OrderEventProducer(
            KafkaTemplate<String, OrderCompletedEvent> kafkaTemplate) {

        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishOrderCompleted(
            OrderCompletedEvent event) {

        kafkaTemplate
                .send(
                        TOPIC,
                        event.orderId().toString(),
                        event
                )
                .join();
    }
}