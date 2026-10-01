package com.telecom.orderservice.event;

public record OrderCreatedEvent(
        Long orderId,
        Long customerId,
        Long mobileLineId,
        Long subscriptionId,
        String type,
        Long requestedPlanId,
        String createdAt
) {
}