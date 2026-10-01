package com.telecom.orderservice.event;

public record OrderCompletedEvent(

        Long orderId,

        Long customerId,

        Long mobileLineId,

        Long subscriptionId,

        String orderType,

        Long requestedPlanId,

        String completedAt

) {
}